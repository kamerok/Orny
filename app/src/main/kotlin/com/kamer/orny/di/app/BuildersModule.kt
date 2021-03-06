package com.kamer.orny.di.app

import com.kamer.orny.di.app.features.*
import com.kamer.orny.presentation.addexpense.AddExpenseActivity
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
    abstract fun injectAddExpense(): AddExpenseActivity

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class, StatisticsModule::class))
    abstract fun injectMain(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(PageSettingsModule::class, AppSettingsModule::class))
    abstract fun injectSettings(): SettingsActivity

}