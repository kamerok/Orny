package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.GetStatisticsInteractor
import com.kamer.orny.interaction.GetStatisticsInteractorImpl
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class StatisticsModule {

    companion object {
        const val STATISTICS = "Statistics"
    }

    @Named(STATISTICS)
    @Provides
    fun provideStatisticsViewModelFactory(viewModel: StatisticsViewModelImpl): ViewModelProvider.Factory
            = viewModel.createFactory()

    @Provides
    fun provideGetStatisticsInteractor(pageRepo: PageRepo, expenseRepo: ExpenseRepo): GetStatisticsInteractor
            = GetStatisticsInteractorImpl(pageRepo, expenseRepo)

}