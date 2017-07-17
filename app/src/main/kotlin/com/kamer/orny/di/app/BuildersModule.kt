package com.kamer.orny.di.app

import com.kamer.orny.di.app.features.AddExpenseModule
import com.kamer.orny.di.app.features.MainModule
import com.kamer.orny.di.app.features.PageSettingsModule
import com.kamer.orny.di.app.features.StatisticsModule
import com.kamer.orny.presentation.addexpense.EditExpenseActivity
import com.kamer.orny.presentation.launch.LoginActivity
import com.kamer.orny.presentation.main.MainActivity
import com.kamer.orny.presentation.settings.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun injectLogin(): LoginActivity

    @ContributesAndroidInjector(modules = arrayOf(AddExpenseModule::class))
    abstract fun injectEditExpense(): EditExpenseActivity

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class, StatisticsModule::class))
    abstract fun injectMain(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(PageSettingsModule::class))
    abstract fun injectSettings(): SettingsActivity

}