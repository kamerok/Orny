package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.app.App
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    abstract fun bindApplicationContext(application: App): Context

}