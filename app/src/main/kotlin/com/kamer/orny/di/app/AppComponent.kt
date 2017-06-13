package com.kamer.orny.di.app

import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.presentation.launch.LaunchActivity
import com.kamer.orny.presentation.main.MainActivity
import dagger.Component

@ApplicationScope
@Component(modules = arrayOf(
        AppModule::class,
        UtilsModule::class,
        NavigationModule::class,
        DataModule::class,
        InteractionModule:: class,
        PresentationModule::class,
        ViewModelModule::class))
interface AppComponent {

    fun inject(launchActivity: LaunchActivity)
    fun inject(editExpenseActivity: EditExpenseActivity)
    fun inject(mainActivity: MainActivity)

}
