package com.kamer.orny.app

import android.app.Activity
import android.app.Application
import com.kamer.orny.di.app.DaggerAppComponent
import com.kamer.orny.utils.installStetho
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject lateinit var injector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        installStetho()

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        Timber.plant(Timber.DebugTree())
    }

    override fun activityInjector(): AndroidInjector<Activity> = injector
}
