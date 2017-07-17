package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.addexpense.CreateExpenseInteractor
import com.kamer.orny.interaction.addexpense.CreateExpenseInteractorImpl
import com.kamer.orny.interaction.addexpense.GetAuthorsInteractor
import com.kamer.orny.interaction.addexpense.GetAuthorsInteractorImpl
import com.kamer.orny.presentation.addexpense.AddExpenseRouter
import com.kamer.orny.presentation.addexpense.AddExpenseRouterImpl
import com.kamer.orny.presentation.addexpense.AddExpenseViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides


@Module
abstract class AddExpenseModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelFactory(lazyViewModel: Lazy<AddExpenseViewModelImpl>): ViewModelProvider.Factory
                = createFactory { lazyViewModel.get() }
    }

    @Binds
    abstract fun bindRouter(router: AddExpenseRouterImpl): AddExpenseRouter

    @Binds
    abstract fun bindGetAuthorsInteractor(interactor: GetAuthorsInteractorImpl): GetAuthorsInteractor

    @Binds
    abstract fun bindCreateExpenseInteractor(interactor: CreateExpenseInteractorImpl): CreateExpenseInteractor

}