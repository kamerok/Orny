package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.model.NewExpense
import com.kamer.orny.interaction.model.AuthorsWithDefault
import io.reactivex.Completable
import io.reactivex.Single


interface AddExpenseInteractor {

    fun getAuthorsWithDefault(): Single<AuthorsWithDefault>

    fun createExpense(expense: NewExpense): Completable

}