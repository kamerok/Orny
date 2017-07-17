package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.addexpense.AddExpenseInteractor
import com.kamer.orny.interaction.addexpense.AddExpenseInteractorImpl
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
    abstract fun bindInteractor(interactor: AddExpenseInteractorImpl): AddExpenseInteractor

}