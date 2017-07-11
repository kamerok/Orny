package com.kamer.orny.presentation.main

import com.kamer.orny.presentation.core.BaseViewModel


class MainViewModelImpl(val mainRouter: MainRouter) : BaseViewModel(), MainViewModel {

    override fun addExpense() = mainRouter.openAddExpenseScreen()

    override fun openSettings() = mainRouter.openSettingsScreen()

}