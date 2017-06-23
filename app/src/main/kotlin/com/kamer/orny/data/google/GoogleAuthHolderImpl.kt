package com.kamer.orny.data.google

import android.Manifest
import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.ReactiveActivities
import com.kamer.orny.presentation.launch.LoginActivity
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.hasPermission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.properties.Delegates


class GoogleAuthHolderImpl(
        private val context: Context,
        private val prefs: Prefs,
        private val activityHolder: ActivityHolder,
        private val reactiveActivities: ReactiveActivities)
    : GoogleAuthHolder {

    companion object {
        private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)
    }

    private var credential: GoogleAccountCredential
            by Delegates.observable(createCredentials()) { _, _, _ ->
                authorizedRelay.accept(!prefs.accountName.isEmpty())
            }

    private val authorizedRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    init {
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

    override fun isAuthorized(): Observable<Boolean> = authorizedRelay.distinctUntilChanged()

    override fun login(): Completable {
        if (authorizedRelay.value) {
            return Completable.complete()
        }
        return checkPermission()
                .andThen(reactiveActivities.chooseGoogleAccount(credential))
                .flatMapCompletable { accountName ->
                    Completable.fromAction {
                        prefs.accountName = accountName
                        credential = createCredentials()
                    }
                }
    }

    override fun logout(): Completable = Completable.fromAction {
        prefs.clear()
        credential = createCredentials()
    }

    override fun getActiveCredentials(): Single<GoogleAccountCredential> =
            checkAuth()
                    .andThen(checkPermission())
                    .toSingle { createCredentials() }

    private fun createCredentials(): GoogleAccountCredential {
        val accountCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList<String>(*SCOPES))
                .setBackOff(ExponentialBackOff())
        accountCredential.selectedAccountName = prefs.accountName
        if (accountCredential.selectedAccountName == null) {
            Timber.e("no account permission")
        }
        return accountCredential
    }

    private fun launchSignInActivity() {
        val activity = activityHolder.getActivity()
        activity?.startActivity(LoginActivity.getIntent(activity))
    }

    private fun checkAuth(): Completable = Completable
            .fromAction {
                if (prefs.accountName.isEmpty()) {
                    launchSignInActivity()
                    throw Exception("Not logged")
                }
            }

    private fun checkPermission() = Completable
            .fromAction {
                if (!context.hasPermission(Manifest.permission.GET_ACCOUNTS)) {
                    throw SecurityException()
                }
            }
            .onErrorResumeNext { throwable ->
                when (throwable) {
                    is SecurityException -> {
                        Completable.create { emitter ->
                            val activity = activityHolder.getActivity()
                            if (activity != null) {
                                activity.runOnUiThread {
                                    RxPermissions(activity)
                                            .request(Manifest.permission.GET_ACCOUNTS)
                                            .observeOn(Schedulers.io())
                                            .subscribe({ granted ->
                                                if (granted)
                                                    emitter.onComplete()
                                                else
                                                    emitter.onError(Exception("Permission rejected"))
                                            }, {
                                                emitter.onError(it)
                                            })
                                }
                            } else {
                                emitter.onError(Exception("Can't request permission, activity == null"))
                            }
                        }
                    }
                    else -> Completable.error(throwable)
                }
            }
}