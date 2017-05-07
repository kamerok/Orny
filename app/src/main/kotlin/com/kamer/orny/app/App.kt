package com.kamer.orny.app

import android.app.Application
import com.kamer.orny.di.app.AppComponent
import com.kamer.orny.di.app.AppModule
import com.kamer.orny.di.app.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

}
