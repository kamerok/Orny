package com.kamer.orny.di.app

import com.kamer.orny.di.app.features.EditExpenseModule
import com.kamer.orny.di.app.features.MainModule
import com.kamer.orny.di.app.features.StatisticsModule
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.presentation.launch.LoginActivity
import com.kamer.orny.presentation.main.MainActivity
import com.kamer.orny.presentation.settings.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun injectLogin(): LoginActivity

    @ContributesAndroidInjector(modules = arrayOf(EditExpenseModule::class))
    abstract fun injectEditExpense(): EditExpenseActivity

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class, StatisticsModule::class))
    abstract fun injectMain(): MainActivity

    @ContributesAndroidInjector
    abstract fun injectSettings(): SettingsActivity

}