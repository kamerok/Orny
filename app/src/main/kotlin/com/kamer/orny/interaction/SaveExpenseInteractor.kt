package com.kamer.orny.interaction

import com.kamer.orny.data.domain.model.NewExpense
import io.reactivex.Completable


interface SaveExpenseInteractor {

    fun saveExpense(expense: NewExpense): Completable

}