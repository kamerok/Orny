package com.kamer.orny.di.app

import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseRouterImpl
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    @Provides
    @ApplicationScope
    fun provideEditExpenseRouterImpl(): EditExpenseRouterImpl = EditExpenseRouterImpl()

    @Provides
    @ApplicationScope
    fun provideEditExpenseRouter(editExpenseRouterImpl: EditExpenseRouterImpl): EditExpenseRouter = editExpenseRouterImpl

}