package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.GetStatisticsInteractor
import com.kamer.orny.interaction.GetStatisticsInteractorImpl
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
abstract class StatisticsModule {

    @Module
    companion object {
        const val STATISTICS = "Statistics"

        @JvmStatic
        @Named(STATISTICS)
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<StatisticsViewModelImpl>): ViewModelProvider.Factory
                = createFactory{ lazyViewModel.get() }
    }

    @Binds
    abstract fun bindGetStatisticsInteractor(interactor: GetStatisticsInteractorImpl): GetStatisticsInteractor

}