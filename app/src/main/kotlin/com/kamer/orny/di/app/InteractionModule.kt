package com.kamer.orny.di.app

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.GetAuthorsInteractorImpl
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.CreateExpenseInteractorImpl
import dagger.Module
import dagger.Provides

@Module
class InteractionModule {

    @Provides
    @ApplicationScope
    fun provideGetAuthorsInteractor(pageRepo: PageRepo): GetAuthorsInteractor
            = GetAuthorsInteractorImpl(pageRepo)

    @Provides
    @ApplicationScope
    fun provideSaveExpenseInteractor(expenseRepo: ExpenseRepo): CreateExpenseInteractor
            = CreateExpenseInteractorImpl(expenseRepo)

}