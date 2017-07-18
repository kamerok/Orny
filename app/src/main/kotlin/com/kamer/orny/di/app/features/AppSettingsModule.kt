package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.interaction.settings.AppSettingsInteractorImpl
import com.kamer.orny.presentation.core.VMProvider
import com.kamer.orny.presentation.settings.AppSettingsViewModel
import com.kamer.orny.presentation.settings.AppSettingsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides

@Module
abstract class AppSettingsModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<AppSettingsViewModelImpl>): VMProvider<AppSettingsViewModel>
                = object : VMProvider<AppSettingsViewModel> {
            override fun get(fragmentActivity: FragmentActivity): AppSettingsViewModel =
                    ViewModelProviders
                            .of(fragmentActivity, createFactory { lazyViewModel.get() })
                            .get(AppSettingsViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindInteractor(interactor: AppSettingsInteractorImpl): AppSettingsInteractor

}