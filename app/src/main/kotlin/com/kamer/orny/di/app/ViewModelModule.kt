package com.kamer.orny.di.app

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.GetStatisticsInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseViewModelImpl
import com.kamer.orny.presentation.main.MainRouter
import com.kamer.orny.presentation.main.MainViewModelImpl
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ViewModelModule {

    companion object {
        const val EDIT_EXPENSE = "EditExpense"
        const val STATISTICS = "Statistics"
        const val MAIN = "Main"
    }

    @Named(EDIT_EXPENSE)
    @Provides
    @ApplicationScope
    fun provideEditExpenseViewModelFactory(errorMessageParser: ErrorMessageParser,
                                           editExpenseRouter: EditExpenseRouter,
                                           getAuthorsInteractor: GetAuthorsInteractor,
                                           createExpenseInteractor: CreateExpenseInteractor): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return EditExpenseViewModelImpl(errorMessageParser, editExpenseRouter, getAuthorsInteractor, createExpenseInteractor) as T
                }
            }

    @Named(STATISTICS)
    @Provides
    @ApplicationScope
    fun provideStatisticsViewModelFactory(getStatisticsInteractor: GetStatisticsInteractor): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return StatisticsViewModelImpl(getStatisticsInteractor) as T
                }
            }

    @Named(MAIN)
    @Provides
    @ApplicationScope
    fun provideMainViewModelFactory(mainRouter: MainRouter): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return MainViewModelImpl(mainRouter) as T
                }
            }

}