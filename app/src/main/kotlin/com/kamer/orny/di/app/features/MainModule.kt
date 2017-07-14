package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.presentation.main.MainRouter
import com.kamer.orny.presentation.main.MainRouterImpl
import com.kamer.orny.presentation.main.MainViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
abstract class MainModule {

    @Module
    companion object {
        const val MAIN = "Main"

        @JvmStatic
        @Named(MAIN)
        @Provides
        fun provideViewModelFactory(viewModel: MainViewModelImpl): ViewModelProvider.Factory
                = viewModel.createFactory()

    }

    @Binds
    abstract fun bindRouter(router: MainRouterImpl): MainRouter

}