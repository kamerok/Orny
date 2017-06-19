package com.kamer.orny.presentation.launch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.AuthRepo
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.utils.toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_launch.*
import timber.log.Timber
import javax.inject.Inject


class LaunchActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, LaunchActivity::class.java)
    }

    @Inject lateinit var authRepo: AuthRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContentView(R.layout.activity_launch)
        signInView.setOnClickListener { login() }
    }

    private fun login() {
        authRepo
                .login()
                .disposeOnDestroy()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    finish()
                }, { error ->
                    Timber.e(error.message, error)
                    toast("The following error occurred:\n" + error)
                })
    }

}
