package com.kamer.orny.presentation.core

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.kamer.orny.data.android.ActivityHolder
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {

    @Inject lateinit var activityHolder: ActivityHolder

    private val compositeDisposable = CompositeDisposable()

    protected fun <T> Single<T>.disposeOnDestroy(): Single<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun <T> Observable<T>.disposeOnDestroy(): Observable<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun Completable.disposeOnDestroy(): Completable = doOnSubscribe { compositeDisposable.add(it) }

    override fun onResume() {
        super.onResume()
        activityHolder.onActivityResumed(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        activityHolder.onActivityDestroyed(this)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityHolder.passActivityResult(requestCode, resultCode, data)
    }

}