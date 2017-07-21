package com.kamer.orny.presentation.core

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kamer.orny.data.android.ActivityHolderSetter
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject



abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }

    @Inject lateinit var activityHolderSetter: ActivityHolderSetter

    private val compositeDisposable = CompositeDisposable()

    protected fun <T> Single<T>.disposeOnDestroy(): Single<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun <T> Observable<T>.disposeOnDestroy(): Observable<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun Completable.disposeOnDestroy(): Completable = doOnSubscribe { compositeDisposable.add(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHolderSetter.onActivityCreated(this)
    }

    override fun onResume() {
        super.onResume()
        activityHolderSetter.onActivityResumed(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        activityHolderSetter.onActivityDestroyed(this)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityHolderSetter.passActivityResult(requestCode, resultCode, data)
    }

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

}