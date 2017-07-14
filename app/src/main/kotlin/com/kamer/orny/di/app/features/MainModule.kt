package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.presentation.main.MainRouter
import com.kamer.orny.presentation.main.MainRouterImpl
import com.kamer.orny.presentation.main.MainViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MainModule {

    companion object {
        const val MAIN = "Main"
    }

    @Named(MAIN)
    @Provides
    fun provideViewModelFactory(viewModel: MainViewModelImpl): ViewModelProvider.Factory
            = viewModel.createFactory()

    @Provides
    fun provideRouter(activityHolder: ActivityHolder): MainRouter
            = MainRouterImpl(activityHolder)

}