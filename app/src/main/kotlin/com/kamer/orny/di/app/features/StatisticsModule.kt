package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.kamer.orny.interaction.statistics.StatisticsInteractor
import com.kamer.orny.interaction.statistics.StatisticsInteractorImpl
import com.kamer.orny.presentation.core.VMProvider
import com.kamer.orny.presentation.statistics.StatisticsViewModel
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides

@Module
abstract class StatisticsModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelProvider(lazyViewModel: Lazy<StatisticsViewModelImpl>): VMProvider<StatisticsViewModel>
                = object : VMProvider<StatisticsViewModel> {
            override fun get(fragmentActivity: FragmentActivity): StatisticsViewModel =
                    ViewModelProviders
                            .of(fragmentActivity, createFactory { lazyViewModel.get() })
                            .get(StatisticsViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindInteractor(interactor: StatisticsInteractorImpl): StatisticsInteractor

}