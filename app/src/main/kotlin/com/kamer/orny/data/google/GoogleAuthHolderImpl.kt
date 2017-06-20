package com.kamer.orny.data.google

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.presentation.launch.LoginActivity
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.hasPermission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.properties.Delegates


class GoogleAuthHolderImpl(
        private val context: Context,
        private val prefs: Prefs,
        private val activityHolder: ActivityHolder)
    : GoogleAuthHolder, ActivityHolder.ActivityResultHandler {

    companion object {
        private const val REQUEST_ACCOUNT_PICKER = 1000

        private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)
    }

    private var credential: GoogleAccountCredential
            by Delegates.observable(createCredentials()) { _, _, _ ->
                authorizedRelay.accept(!prefs.accountName.isEmpty())
            }

    private val authorizedRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    private var loginListener: LoginListener? = null

    init {
        activityHolder.addActivityResultHandler(this)
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

    override fun isAuthorized(): Observable<Boolean> = authorizedRelay.distinctUntilChanged()

    override fun login(): Completable =
            Completable.create { e ->
                loginListener = object : LoginListener {

                    override fun onError(t: Throwable) {
                        e.onError(t)
                    }

                    override fun onSuccess() {
                        e.onComplete()
                    }

                }
                if (authorizedRelay.value) {
                    loginListener?.onSuccess()
                    return@create
                }
                val activity = activityHolder.getActivity()
                if (activity == null) {
                    loginListener?.onError(Exception("Can't login. Activity == null"))
                } else {
                    activity.runOnUiThread {
                        RxPermissions(activity)
                                .request(Manifest.permission.GET_ACCOUNTS)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(Schedulers.io())
                                .subscribe({ granted ->
                                    if (granted) {
                                        activity.startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
                                    } else {
                                        loginListener?.onError(SecurityException("Account permission denied"))
                                    }
                                })
                    }
                }
            }

    override fun logout(): Completable = Completable.fromAction {
        prefs.clear()
        credential = createCredentials()
    }

    override fun getSheetsService(): Single<Sheets> = getActiveCredentials().map(this::createSheetsService)

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
            when (requestCode) {
                REQUEST_ACCOUNT_PICKER -> {
                    if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                        val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                        if (accountName != null) {
                            prefs.accountName = accountName
                            credential = createCredentials()
                            loginListener?.onSuccess()
                        }
                    } else {
                        loginListener?.onError(Exception("Pick account failed"))
                    }
                    true
                }
                else -> false
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

    private fun getActiveCredentials(): Single<GoogleAccountCredential> = Completable
            .fromAction {
                if (prefs.accountName.isEmpty()) {
                    launchSignInActivity()
                    throw Exception("Not logged")
                }
            }
            .andThen(
                    Completable.fromAction {
                        val activity = activityHolder.getActivity()
                        if (activity != null && !activity.hasPermission(Manifest.permission.GET_ACCOUNTS)) {
                            activity.runOnUiThread {
                                RxPermissions(activity)
                                        .request(Manifest.permission.GET_ACCOUNTS)
                                        .subscribe()
                            }
                            throw Exception("No permission")
                        }
                    }
            )
            .toSingle { createCredentials() }

    private fun launchSignInActivity() {
        val activity = activityHolder.getActivity()
        activity?.startActivity(LoginActivity.getIntent(activity))
    }


    private fun createSheetsService(credential: GoogleAccountCredential): Sheets {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        return Sheets.Builder(transport, jsonFactory, credential).build()
    }

    private interface LoginListener {

        fun onError(t: Throwable)

        fun onSuccess()

    }
}