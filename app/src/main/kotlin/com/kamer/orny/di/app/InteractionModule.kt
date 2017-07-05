package com.kamer.orny.di.app

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.*
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

    @Provides
    @ApplicationScope
    fun provideGetStatisticsInteractor(pageRepo: PageRepo, expenseRepo: ExpenseRepo): GetStatisticsInteractor
            = GetStatisticsInteractorImpl(pageRepo, expenseRepo)
}