package com.kamer.orny.presentation.main

import com.kamer.orny.presentation.core.BaseViewModel
import javax.inject.Inject


class MainViewModelImpl @Inject constructor(
        val mainRouter: MainRouter
) : BaseViewModel(), MainViewModel {

    override fun addExpense() = mainRouter.openAddExpenseScreen()

    override fun openSettings() = mainRouter.openSettingsScreen()

}