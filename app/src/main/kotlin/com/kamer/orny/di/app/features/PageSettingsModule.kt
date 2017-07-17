package com.kamer.orny.di.app.features

import com.kamer.orny.interaction.settings.GetPageSettingsInteractor
import com.kamer.orny.interaction.settings.GetPageSettingsInteractorImpl
import com.kamer.orny.interaction.settings.SavePageSettingsInteractor
import com.kamer.orny.interaction.settings.SavePageSettingsInteractorImpl
import com.kamer.orny.presentation.settings.PageSettingsViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
abstract class PageSettingsModule {

    @Module
    companion object {
        const val PAGE_SETTINGS = "PageSettings"

        @JvmStatic
        @Named(PAGE_SETTINGS)
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<PageSettingsViewModelImpl>)
                = createFactory { lazyViewModel.get() }
    }

    @Binds
    abstract fun bindGetInteractor(interactor: GetPageSettingsInteractorImpl): GetPageSettingsInteractor

    @Binds
    abstract fun bindSaveInteractor(interactor: SavePageSettingsInteractorImpl): SavePageSettingsInteractor

}