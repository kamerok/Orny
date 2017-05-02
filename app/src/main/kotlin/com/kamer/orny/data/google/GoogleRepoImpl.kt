package com.kamer.orny.data.google

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.hasPermisstion
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.Delegates


class GoogleRepoImpl(private val context: Context, val prefs: Prefs) : GoogleRepo {

    companion object {
        private const val REQUEST_ACCOUNT_PICKER = 1000
    }

    private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS_READONLY)

    private var credential: GoogleAccountCredential by Delegates.observable(createCredentials()) { _, _, _ ->
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

    private val authorizedRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    private lateinit var activityRef: WeakReference<Activity>

    private var loginListener: LoginListener? = null

    init {
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

    override fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
        //constantly check permission
        if (!context.hasPermisstion(Manifest.permission.ACCOUNT_MANAGER)) {
            RxPermissions(activity)
                    .request(Manifest.permission.GET_ACCOUNTS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribe({ granted ->
                        if (granted) {
                            credential = createCredentials()
                        }
                    })
        }
    }

    override fun passActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ACCOUNT_PICKER ->
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
        }
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
                val activity = activityRef.get()
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

    override fun getData(): Single<List<String>> = Single.fromCallable { getDataFromApi() }

    private fun createCredentials(): GoogleAccountCredential {
        val accountCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList<String>(*SCOPES))
                .setBackOff(ExponentialBackOff())
        accountCredential.selectedAccountName = prefs.accountName
        return accountCredential
    }

    private fun getDataFromApi(): List<String> {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        val service = Sheets.Builder(transport, jsonFactory, credential)
                .build()
        val results = ArrayList<String>()
        val response = service.spreadsheets().get("1YsFrfpNzs_gjdtnqVNuAPPYl3NRjeo8GgEWAOD7BdOg")
                .execute()
        val values = response.values
        values.mapTo(results, Any::toString)
        Log.d("Results", results.toString())
        return results
    }

    private interface LoginListener {

        fun onError(t: Throwable)

        fun onSuccess()

    }

}