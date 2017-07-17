package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.NewExpense
import io.reactivex.Completable
import io.reactivex.Single


interface AddExpenseInteractor {

    fun getAuthors(): Single<List<Author>>

    fun createExpense(expense: NewExpense): Completable

}