package com.kamer.orny.di.app

import com.kamer.orny.di.presenter.PresenterComponent
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.presentation.launch.LaunchActivity
import dagger.Component

@ApplicationScope
@Component(modules = arrayOf(
        AppModule::class,
        UtilsModule::class,
        NavigationModule::class,
        DataModule::class,
        InteractionModule:: class,
        PresentationModule::class))
interface AppComponent {

    fun presenterComponent(): PresenterComponent

    fun inject(launchActivity: LaunchActivity)
    fun inject(editExpenseActivity: EditExpenseActivity)

}
