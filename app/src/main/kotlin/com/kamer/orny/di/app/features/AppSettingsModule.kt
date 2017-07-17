package com.kamer.orny.di.app.features

import com.kamer.orny.interaction.settings.AppSettingsInteractor
import com.kamer.orny.interaction.settings.AppSettingsInteractorImpl
import com.kamer.orny.presentation.settings.AppSettingsViewModelImpl
import com.kamer.orny.presentation.settings.PageSettingsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
abstract class AppSettingsModule {

    @Module
    companion object {
        const val APP_SETTINGS = "AppSettings"

        @JvmStatic
        @Named(APP_SETTINGS)
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<AppSettingsViewModelImpl>)
                = createFactory { lazyViewModel.get() }
    }

    @Binds
    abstract fun bindInteractor(interactor: AppSettingsInteractorImpl): AppSettingsInteractor

}