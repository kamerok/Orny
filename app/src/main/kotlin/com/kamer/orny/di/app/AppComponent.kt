package com.kamer.orny.di.app

import com.kamer.orny.di.presenter.PresenterComponent
import com.kamer.orny.presentation.launch.LaunchActivity
import dagger.Component

@ApplicationScope
@Component(modules = arrayOf(AppModule::class, DataModule::class, UtilsModule::class, PresentationModule::class))
interface AppComponent {

    fun presenterComponent(): PresenterComponent

    fun inject(launchActivity: LaunchActivity)

}
