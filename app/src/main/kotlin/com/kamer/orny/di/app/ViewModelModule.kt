package com.kamer.orny.di.app

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import com.kamer.orny.presentation.editexpense.EditExpenseViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ViewModelModule {

    companion object {
        const val EDIT_EXPENSE = "EditExpense"
    }

    @Named(EDIT_EXPENSE)
    @Provides
    @ApplicationScope
    fun provideEditExpenseViewModelFactory(errorMessageParser: ErrorMessageParser,
                                           editExpenseRouter: EditExpenseRouter,
                                           getAuthorsInteractor: GetAuthorsInteractor,
                                           saveExpenseInteractor: SaveExpenseInteractor): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return EditExpenseViewModelImpl(errorMessageParser, editExpenseRouter, getAuthorsInteractor, saveExpenseInteractor) as T
                }
            }

}