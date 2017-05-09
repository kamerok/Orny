package com.kamer.orny.di.app

import com.kamer.orny.data.ExpenseRepo
import com.kamer.orny.data.PageRepo
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.GetAuthorsInteractorImpl
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.interaction.SaveExpenseInteractorImpl
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
    fun provideSaveExpenseInteractor(expenseRepo: ExpenseRepo): SaveExpenseInteractor
            = SaveExpenseInteractorImpl(expenseRepo)

}