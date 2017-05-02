package com.kamer.orny.di

import com.kamer.orny.LaunchActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, DataModule::class, UtilsModule::class))
interface AppComponent {

    fun inject(launchActivity: LaunchActivity)

}
