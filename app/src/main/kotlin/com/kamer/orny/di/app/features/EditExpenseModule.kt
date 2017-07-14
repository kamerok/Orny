package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.CreateExpenseInteractorImpl
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.GetAuthorsInteractorImpl
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseRouterImpl
import com.kamer.orny.presentation.editexpense.EditExpenseViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
abstract class EditExpenseModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideViewModelFactory(viewModel: EditExpenseViewModelImpl): ViewModelProvider.Factory
                = viewModel.createFactory()
    }

    @Binds
    abstract fun bindRouter(router: EditExpenseRouterImpl): EditExpenseRouter

    @Binds
    abstract fun bindGetAuthorsInteractor(interactor: GetAuthorsInteractorImpl): GetAuthorsInteractor

    @Binds
    abstract fun bindSaveExpenseInteractor(interactor: CreateExpenseInteractorImpl): CreateExpenseInteractor

}