package com.kamer.orny

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.activity_launch.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class LaunchActivity : AppCompatActivity() {

    private val REQUEST_ACCOUNT_PICKER = 1000
    private val REQUEST_AUTHORIZATION = 1001
    private val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    private val PREF_ACCOUNT_NAME = "accountName"

    private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS_READONLY)

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
            MakeRequestTask(credential).execute()
        }
    }

    private fun chooseAccount() {
        //TODO: request get account permission
        val accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null)
        if (accountName != null) {
            credential.selectedAccountName = accountName
            getResultsFromApi()
        } else {
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
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

    private inner class MakeRequestTask internal constructor(credential: GoogleAccountCredential) : AsyncTask<Void, Void, ArrayList<String>>() {

        private var mService: Sheets? = null
        private var mLastError: Exception? = null

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build()
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void): ArrayList<String>? {
            try {
                return ArrayList(getDataFromApi())
            } catch (e: Exception) {
                mLastError = e
                cancel(true)
                return null
            }

        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * *
         * @throws IOException
         */
        private fun getDataFromApi(): ArrayList<String>? {
            val spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
            val range = "Class Data!A2:E"
            val results = ArrayList<String>()
            val response = this.mService!!.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute()
            val values = response.getValues()
            if (values != null) {
                results.add("Name, Major")
                values.mapTo(results) { it[0].toString() + ", " + it[4] }
            }
            return results
        }

        override fun onPreExecute() {
            progressView.visible()
        }

        override fun onPostExecute(output: ArrayList<String>?) {
            progressView.gone()
            if (output == null || output.size == 0) {
                toast("No results returned.")
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:")
                textView.text = TextUtils.join("\n", output)
            }
        }

        override fun onCancelled() {
            progressView.gone()
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            (mLastError as GooglePlayServicesAvailabilityIOException).connectionStatusCode)
                } else if (mLastError is UserRecoverableAuthIOException) {
                    startActivityForResult((mLastError as UserRecoverableAuthIOException).intent, REQUEST_AUTHORIZATION)
                } else {
                    toast("The following error occurred:\n" + mLastError)
                }
            } else {
                toast("Request cancelled.")
            }
        }
    }
}
