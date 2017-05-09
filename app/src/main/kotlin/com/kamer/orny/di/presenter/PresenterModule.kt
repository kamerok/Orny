package com.kamer.orny.di.presenter

import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.EditExpensePresenter
import com.kamer.orny.presentation.editexpense.EditExpenseRouter
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    @PresenterScope
    fun provideEditExpensePresenter(errorMessageParser: ErrorMessageParser,
                                    editExpenseRouter: EditExpenseRouter,
                                    getAuthorsInteractor: GetAuthorsInteractor,
                                    saveExpenseInteractor: SaveExpenseInteractor)
            = EditExpensePresenter(errorMessageParser, editExpenseRouter, getAuthorsInteractor, saveExpenseInteractor)

}