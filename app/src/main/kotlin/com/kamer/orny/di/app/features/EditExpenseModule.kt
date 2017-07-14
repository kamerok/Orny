package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.CreateExpenseInteractorImpl
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.GetAuthorsInteractorImpl
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseRouterImpl
import com.kamer.orny.presentation.editexpense.EditExpenseViewModelImpl
import com.kamer.orny.utils.createFactory
import dagger.Module
import dagger.Provides

@Module
class EditExpenseModule {

    @Provides
    fun provideViewModelFactory(viewModel: EditExpenseViewModelImpl): ViewModelProvider.Factory
            = viewModel.createFactory()

    @Provides
    fun provideGetAuthorsInteractor(pageRepo: PageRepo): GetAuthorsInteractor
            = GetAuthorsInteractorImpl(pageRepo)

    @Provides
    fun provideSaveExpenseInteractor(expenseRepo: ExpenseRepo): CreateExpenseInteractor
            = CreateExpenseInteractorImpl(expenseRepo)

    @Provides
    fun provideRouter(activityHolder: ActivityHolder): EditExpenseRouter
            = EditExpenseRouterImpl(activityHolder)

}