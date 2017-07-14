package com.kamer.orny.di.app

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.presentation.editexpense.EditExpenseViewModelImpl
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
    fun provideEditExpenseViewModelFactory(viewModel: EditExpenseViewModelImpl): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModel as T
                }
            }

    @Named(STATISTICS)
    @Provides
    @ApplicationScope
    fun provideStatisticsViewModelFactory(viewModel: StatisticsViewModelImpl): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModel as T
                }
            }

    @Named(MAIN)
    @Provides
    @ApplicationScope
    fun provideMainViewModelFactory(viewModel: MainViewModelImpl): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModel as T
                }
            }

}