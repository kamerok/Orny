package com.kamer.orny.di.app

import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseRouterImpl
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {

    @Provides
    @ApplicationScope
    fun provideEditExpenseRouter(activityHolder: ActivityHolder): EditExpenseRouter
            = EditExpenseRouterImpl(activityHolder)

}