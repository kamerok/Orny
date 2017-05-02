package com.kamer.orny

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.kamer.orny.app.App
import com.kamer.orny.data.AuthRepo
import com.kamer.orny.data.SpreadsheetRepo
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.utils.gone
import com.kamer.orny.utils.toast
import com.kamer.orny.utils.visible
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_launch.*
import javax.inject.Inject


class LaunchActivity : AppCompatActivity() {

    @Inject lateinit var googleRepo: GoogleRepo
    @Inject lateinit var authRepo: AuthRepo
    @Inject lateinit var spreadsheetRepo: SpreadsheetRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContentView(R.layout.activity_launch)
        signInView.setOnClickListener { login() }
        signOutView.setOnClickListener { logout() }
        requestView.setOnClickListener { getResultsFromApi() }
    }

    override fun onResume() {
        super.onResume()
        googleRepo.setActivity(this)
        authRepo.isAuthorized()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({authorized ->
                    authorizedView.text = if (authorized) "Authorized" else "Not authorized"
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleRepo.passActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getResultsFromApi() {
        spreadsheetRepo.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressView.visible() }
                .doFinally { progressView.gone() }
                .subscribe({ list ->
                    if (list == null || list.isEmpty()) {
                        toast("No results returned.")
                    } else {
                        val result = ArrayList(list)
                        result.add(0, "Data retrieved using the Google Sheets API:")
                        textView.text = TextUtils.join("\n", list)
                    }
                }, { error ->
                    Log.d("Error", error.message, error)
                    toast("The following error occurred:\n" + error)
                })
    }

    private fun logout() {
        authRepo
                .logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressView.visible() }
                .doFinally { progressView.gone() }
                .subscribe({}, { error ->
                    Log.d("Error", error.message, error)
                    toast("The following error occurred:\n" + error)
                })
    }

    private fun login() {
        authRepo
                .login()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressView.visible() }
                .doFinally { progressView.gone() }
                .subscribe({}, { error ->
                    Log.d("Error", error.message, error)
                    toast("The following error occurred:\n" + error)
                })
    }

}
