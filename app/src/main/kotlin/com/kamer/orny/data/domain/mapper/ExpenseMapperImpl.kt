package com.kamer.orny.data.domain.mapper

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.query.ExpenseWithEntities
import com.kamer.orny.di.app.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class ExpenseMapperImpl @Inject constructor() : ExpenseMapper {

    override fun toGoogleExpense(expense: Expense): GoogleExpense {
        val values = mutableListOf<Double>()
        for ((key, value) in expense.values) {
            while (values.size < key.position) {
                values.add(0.0)
            }
            values.add(key.position, value)
        }
        return GoogleExpense(
                id = expense.id,
                comment = expense.comment,
                date = expense.date,
                isOffBudget = expense.isOffBudget,
                values = values
        )
    }

    override fun toExpense(expenseWithEntities: ExpenseWithEntities, authorsByIds: Map<String, AuthorEntity>): Expense {
        return Expense(
                id = expenseWithEntities.expense.id,
                comment = expenseWithEntities.expense.comment,
                isOffBudget = expenseWithEntities.expense.isOffBudget,
                date = expenseWithEntities.expense.date,
                values = expenseWithEntities.entries.associate { authorsByIds[it.authorId]!!.toAuthor() to it.amount }
        )
    }
}