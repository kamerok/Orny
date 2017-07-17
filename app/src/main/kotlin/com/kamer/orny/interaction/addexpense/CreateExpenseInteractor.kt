package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.model.NewExpense
import io.reactivex.Completable


interface CreateExpenseInteractor {

    fun createExpense(expense: NewExpense): Completable

}