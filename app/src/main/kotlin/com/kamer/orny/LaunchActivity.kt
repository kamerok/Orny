package com.kamer.orny

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.kamer.orny.utils.gone
import com.kamer.orny.utils.toast
import com.kamer.orny.utils.visible
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_launch.*
import java.util.*
import kotlin.collections.ArrayList


class LaunchActivity : AppCompatActivity() {

    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    private val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    private val PREF_ACCOUNT_NAME = "accountName"

    private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS_READONLY)

    private val rxPermissions by lazy { RxPermissions(this) }
    private val googleApiAvailability by lazy { GoogleApiAvailability.getInstance() }
    private val credential by lazy {
        GoogleAccountCredential.usingOAuth2(
                applicationContext, Arrays.asList<String>(*SCOPES))
                .setBackOff(ExponentialBackOff())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        signInView.setOnClickListener { getResultsFromApi() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES ->
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi()
                } else {
                    toast("This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app.")
                }
            REQUEST_ACCOUNT_PICKER ->
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    if (accountName != null) {
                        getPreferences(Context.MODE_PRIVATE)
                                .edit()
                                .putString(PREF_ACCOUNT_NAME, accountName)
                                .apply()
                        credential.selectedAccountName = accountName
                        getResultsFromApi()
                    }
                }
            REQUEST_AUTHORIZATION ->
                if (resultCode == Activity.RESULT_OK) {
                    getResultsFromApi()
                }
        }
    }

    private fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (credential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            toast("No network connection available.")
        } else {
            makeRequest()
        }
    }

    private fun chooseAccount() {
        rxPermissions
                .request(Manifest.permission.GET_ACCOUNTS)
                .subscribe {
                    if (it) {
                        val accountName = getPreferences(Context.MODE_PRIVATE)
                                .getString(PREF_ACCOUNT_NAME, null)
                        if (accountName != null) {
                            credential.selectedAccountName = accountName
                            getResultsFromApi()
                        } else {
                            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
                        }
                    }
                }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val connectionStatusCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val connectionStatusCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (googleApiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    private fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        googleApiAvailability
                .getErrorDialog(this,
                        connectionStatusCode,
                        REQUEST_GOOGLE_PLAY_SERVICES)
                .show()
    }

    private fun isDeviceOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun makeRequest() {
        Single
                .fromCallable { ArrayList(getDataFromApi()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressView.visible() }
                .doFinally { progressView.gone() }
                .subscribe(
                        { list ->
                            if (list == null || list.size == 0) {
                                toast("No results returned.")
                            } else {
                                list.add(0, "Data retrieved using the Google Sheets API:")
                                textView.text = TextUtils.join("\n", list)
                            }
                        },
                        { error ->
                            if (error != null) {
                                if (error is GooglePlayServicesAvailabilityIOException) {
                                    showGooglePlayServicesAvailabilityErrorDialog(error.connectionStatusCode)
                                } else if (error is UserRecoverableAuthIOException) {
                                    startActivityForResult(error.intent, REQUEST_AUTHORIZATION)
                                } else {
                                    Log.d("Error", error.message, error)
                                    toast("The following error occurred:\n" + error)
                                }
                            } else {
                                toast("Request cancelled.")
                            }
                        })
    }

    private fun getDataFromApi(): ArrayList<String> {
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

}
