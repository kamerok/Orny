package com.kamer.orny.data.domain.mapper

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.query.ExpenseWithEntities


interface ExpenseMapper {

    fun toGoogleExpense(expense: Expense): GoogleExpense

    fun toExpense(expenseWithEntities: ExpenseWithEntities, authorsByIds: Map<String, AuthorEntity>): Expense

}