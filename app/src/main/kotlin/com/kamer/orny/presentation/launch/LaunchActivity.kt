package com.kamer.orny.presentation.launch

import android.content.Intent
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.AuthRepo
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.utils.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_launch.*
import timber.log.Timber
import javax.inject.Inject


class LaunchActivity : BaseActivity() {

    @Inject lateinit var googleRepo: GoogleRepo
    @Inject lateinit var authRepo: AuthRepo

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        authRepo
                .isAuthorized()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ authorized ->
                    if (authorized) {
                        startActivity(EditExpenseActivity.getIntent(this))
                        finish()
                    }
                })
        setContentView(R.layout.activity_launch)
        signInView.setOnClickListener { login() }
    }

    override fun onResume() {
        super.onResume()
        googleRepo.setActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleRepo.passActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun login() {
        authRepo
                .login()
                .doOnSubscribe { compositeDisposable.add(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { error ->
                    Timber.e(error.message, error)
                    toast("The following error occurred:\n" + error)
                })
    }

}
