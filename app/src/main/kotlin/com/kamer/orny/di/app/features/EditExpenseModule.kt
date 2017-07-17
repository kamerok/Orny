package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.addexpense.CreateExpenseInteractor
import com.kamer.orny.interaction.addexpense.CreateExpenseInteractorImpl
import com.kamer.orny.interaction.addexpense.GetAuthorsInteractor
import com.kamer.orny.interaction.addexpense.GetAuthorsInteractorImpl
import com.kamer.orny.presentation.addexpense.EditExpenseRouter
import com.kamer.orny.presentation.addexpense.EditExpenseRouterImpl
import com.kamer.orny.presentation.addexpense.EditExpenseViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides


@Module
abstract class EditExpenseModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<EditExpenseViewModelImpl>): ViewModelProvider.Factory
                = createFactory { lazyViewModel.get() }
    }

    @Binds
    abstract fun bindRouter(router: EditExpenseRouterImpl): EditExpenseRouter

    @Binds
    abstract fun bindGetAuthorsInteractor(interactor: GetAuthorsInteractorImpl): GetAuthorsInteractor

    @Binds
    abstract fun bindCreateExpenseInteractor(interactor: CreateExpenseInteractorImpl): CreateExpenseInteractor

}