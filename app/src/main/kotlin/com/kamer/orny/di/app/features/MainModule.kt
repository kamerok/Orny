package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.kamer.orny.interaction.main.MainInteractor
import com.kamer.orny.interaction.main.MainInteractorImpl
import com.kamer.orny.presentation.core.VMProvider
import com.kamer.orny.presentation.main.MainRouter
import com.kamer.orny.presentation.main.MainRouterImpl
import com.kamer.orny.presentation.main.MainViewModel
import com.kamer.orny.presentation.main.MainViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides

@Module
abstract class MainModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelProvider(lazyViewModel: Lazy<MainViewModelImpl>): VMProvider<MainViewModel>
                = object : VMProvider<MainViewModel> {
            override fun get(fragmentActivity: FragmentActivity): MainViewModel {
                return ViewModelProviders
                        .of(fragmentActivity, createFactory { lazyViewModel.get() })
                        .get(MainViewModelImpl::class.java)
            }
        }

    }

    @Binds
    abstract fun bindRouter(router: MainRouterImpl): MainRouter

    @Binds
    abstract fun bindInteractor(interactor: MainInteractorImpl): MainInteractor

}