package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter


@InjectViewState
class EditExpensePresenter : MvpPresenter<EditExpenseView>() {

    override fun onFirstViewAttach() {
        viewState.showError("Test")
    }

}