package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.app.App
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideApplicationContext(application: App): Context = application

}