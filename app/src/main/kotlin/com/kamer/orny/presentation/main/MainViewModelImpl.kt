package com.kamer.orny.presentation.main

import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.interaction.main.MainInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import timber.log.Timber
import javax.inject.Inject


class MainViewModelImpl @Inject constructor(
        val mainRouter: MainRouter,
        val interactor: MainInteractor
) : BaseViewModel(), MainViewModel {

    override val updateProgressStream = MutableLiveData<Boolean>().apply { value = false }

    init {
        interactor
                .updatePage()
                .disposeOnDestroy()
                .doOnSubscribe { updateProgressStream.value = true }
                .doFinally { updateProgressStream.value = false }
                .subscribe({}, { Timber.e(it) })
    }

    override fun addExpense() = mainRouter.openAddExpenseScreen()

    override fun openSettings() = mainRouter.openSettingsScreen()

}