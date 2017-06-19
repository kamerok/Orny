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
import com.google.api.services.sheets.v4.model.ValueRange
import com.jakewharton.rxrelay2.BehaviorRelay
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.model.Author
import com.kamer.orny.data.model.Expense
import com.kamer.orny.utils.Prefs
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class GoogleRepoImpl(private val context: Context, val prefs: Prefs, val activityHolder: ActivityHolder)
    : GoogleRepo, ActivityHolder.ActivityResultHandler {

    companion object {
        private const val REQUEST_ACCOUNT_PICKER = 1000

        private const val SPREADSHEET_ID = "1YsFrfpNzs_gjdtnqVNuAPPYl3NRjeo8GgEWAOD7BdOg"
        private const val SHEET_NAME = "Тест"

        private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS)

    private var credential: GoogleAccountCredential by Delegates.observable(createCredentials()) { _, _, _ ->
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

    private val authorizedRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    private var loginListener: LoginListener? = null

    init {
        activityHolder.addActivityResultHandler(this)
        authorizedRelay.accept(!prefs.accountName.isEmpty())
    }

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

    override fun getAllExpenses(): Single<List<Expense>> = Single.fromCallable {
        val list = mutableListOf<Expense>()

        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        val service = Sheets.Builder(transport, jsonFactory, credential)
                .build()

        val response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, SHEET_NAME + "!A11:E")
                .execute()
        for (value in response.getValues()) {
            Timber.d(value.toString())
            if (value.isNotEmpty()) {
                val expense = expenseFromList(value)
                Timber.d(expense.toString())
                list.add(expense)
            }
        }

        return@fromCallable list
    }

    override fun addExpense(expense: Expense): Completable =
            Completable.fromAction { addExpenseToApi(expense) }

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

    private fun addExpenseToApi(expense: Expense) {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        val service = Sheets.Builder(transport, jsonFactory, credential)
                .build()
        val writeData: MutableList<MutableList<Any>> = ArrayList()
        writeData.add(expenseToList(expense))
        val valueRange = ValueRange()
        valueRange.setValues(writeData)
        valueRange.majorDimension = "ROWS"
        val request = service.spreadsheets().values().append(SPREADSHEET_ID, SHEET_NAME, valueRange).setValueInputOption("USER_ENTERED")
        val response = request.execute()
        Timber.d(response.toString())
    }

    private fun expenseToList(expense: Expense): MutableList<Any> {
        val dataRow: MutableList<Any> = ArrayList()
        dataRow.add(expense.comment)
        dataRow.add(DATE_FORMAT.format(expense.date))
        dataRow.add(if (expense.isOffBudget) "1" else "0")
        if (expense.author?.id != "0") {
            dataRow.add("")
        }
        dataRow.add(expense.amount.toString())
        return dataRow
    }

    private fun expenseFromList(list: MutableList<Any>): Expense {
        val comment = list[0].toString()
        val date = try {
            DATE_FORMAT.parse(list[1].toString())
        } catch (e: ParseException) {
            Date()
        }
        val isOffBudget = list[2].toString() == "1"
        val authorId = if (list[3].toString().isNullOrEmpty()) "1" else "0"
        val amount = if (list[3].toString().isNullOrEmpty()) list[4].toString().toDouble() else list[3].toString().toDouble()
        return Expense(
                comment = comment,
                date = date,
                isOffBudget = isOffBudget,
                author = Author(id = authorId, name = if (authorId == "0") "Лена" else "Макс", color = ""),
                amount = amount
        )
    }

    private interface LoginListener {

        fun onError(t: Throwable)

        fun onSuccess()

    }

}