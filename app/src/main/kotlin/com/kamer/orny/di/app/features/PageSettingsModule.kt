package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.kamer.orny.interaction.settings.PageSettingsInteractor
import com.kamer.orny.interaction.settings.PageSettingsInteractorImpl
import com.kamer.orny.presentation.core.VMProvider
import com.kamer.orny.presentation.settings.PageSettingsViewModel
import com.kamer.orny.presentation.settings.PageSettingsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides

@Module
abstract class PageSettingsModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelProvider(lazyViewModel: Lazy<PageSettingsViewModelImpl>): VMProvider<PageSettingsViewModel>
                = object : VMProvider<PageSettingsViewModel> {
            override fun get(fragmentActivity: FragmentActivity): PageSettingsViewModel =
                    ViewModelProviders
                            .of(fragmentActivity, createFactory { lazyViewModel.get() })
                            .get(PageSettingsViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindInteractor(interactor: PageSettingsInteractorImpl): PageSettingsInteractor

}