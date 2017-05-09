package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser


@InjectViewState
class EditExpensePresenter(val errorParser: ErrorMessageParser,
                           val authorsInteractor: GetAuthorsInteractor,
                           val saveExpenseInteractor: SaveExpenseInteractor
) : MvpPresenter<EditExpenseView>() {

    override fun onFirstViewAttach() {

    }

}