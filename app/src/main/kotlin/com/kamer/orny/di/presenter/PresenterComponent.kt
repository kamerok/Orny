package com.kamer.orny.di.presenter

import com.kamer.orny.presentation.editexpense.EditExpensePresenter
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = arrayOf(PresenterModule::class))
interface PresenterComponent {

    fun editExpensePresenter(): EditExpensePresenter

}