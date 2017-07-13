package com.kamer.orny.di.app

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

    @ContributesAndroidInjector
    abstract fun injectEditExpense(): EditExpenseActivity

    @ContributesAndroidInjector
    abstract fun injectMain(): MainActivity

    @ContributesAndroidInjector
    abstract fun injectSettings(): SettingsActivity

}