package com.kamer.orny.di.app.features

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.kamer.orny.interaction.addexpense.AddExpenseInteractor
import com.kamer.orny.interaction.addexpense.AddExpenseInteractorImpl
import com.kamer.orny.presentation.addexpense.AddExpenseRouter
import com.kamer.orny.presentation.addexpense.AddExpenseRouterImpl
import com.kamer.orny.presentation.addexpense.AddExpenseViewModel
import com.kamer.orny.presentation.addexpense.AddExpenseViewModelImpl
import com.kamer.orny.presentation.core.VMProvider
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
        fun provideViewModelProvider(lazyViewModel: Lazy<AddExpenseViewModelImpl>): VMProvider<AddExpenseViewModel>
                = object : VMProvider<AddExpenseViewModel> {
            override fun get(fragmentActivity: FragmentActivity): AddExpenseViewModel =
                    ViewModelProviders
                            .of(fragmentActivity, createFactory { lazyViewModel.get() })
                            .get(AddExpenseViewModelImpl::class.java)
        }
    }

    @Binds
    abstract fun bindRouter(router: AddExpenseRouterImpl): AddExpenseRouter

    @Binds
    abstract fun bindInteractor(interactor: AddExpenseInteractorImpl): AddExpenseInteractor

}