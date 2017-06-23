package com.kamer.orny.data.google

import android.Manifest
import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.ReactiveActivities
import com.kamer.orny.presentation.launch.LoginActivity
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.hasPermission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


class GoogleAuthHolderImpl(
        private val context: Context,
        private val prefs: Prefs,
        private val activityHolder: ActivityHolder,
        private val reactiveActivities: ReactiveActivities)
    : GoogleAuthHolder {

    companion object {
        private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)
    }

    override fun login(): Completable {
        if (prefs.accountName.isNotEmpty()) {
            return Completable.complete()
        }
        return checkPermission()
                .andThen(reactiveActivities.chooseGoogleAccount(createCredentials()))
                .flatMapCompletable { accountName ->
                    Completable.fromAction {
                        prefs.accountName = accountName
                    }
                }
    }

    override fun logout(): Completable = Completable.fromAction {
        prefs.accountName = ""
    }

    override fun getActiveCredentials(): Single<GoogleAccountCredential> =
            checkAuth()
                    .andThen(checkPermission())
                    .toSingle { createCredentials() }
                    .flatMap { credential ->
                        checkIfAccountExist(credential)
                                .toSingle { credential }
                    }

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

    private fun checkIfAccountExist(credential: GoogleAccountCredential): Completable = Completable
            .fromAction {
                if (context.hasPermission(Manifest.permission.GET_ACCOUNTS) && credential.selectedAccountName.isNullOrEmpty()) {
                    prefs.accountName = ""
                    launchSignInActivity()
                    throw Exception("Account not exist")
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