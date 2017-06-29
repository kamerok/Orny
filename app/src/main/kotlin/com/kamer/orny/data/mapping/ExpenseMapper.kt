package com.kamer.orny.data.mapping

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.Expense
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ExpenseMapper {
    companion object {
        val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
}

fun Expense.toList(): MutableList<Any> {
    val dataRow: MutableList<Any> = ArrayList()
    dataRow.add(comment)
    dataRow.add(ExpenseMapper.DATE_FORMAT.format(date))
    dataRow.add(if (isOffBudget) "1" else "0")
    if (author?.id != "0") {
        dataRow.add("")
    }
    dataRow.add(amount.toString())
    return dataRow
}

fun MutableList<Any>.toExpense(): Expense {
    val comment = this[0].toString()
    val date = try {
        ExpenseMapper.DATE_FORMAT.parse(this[1].toString())
    } catch (e: ParseException) {
        Date()
    }
    val isOffBudget = this[2].toString() == "1"
    val authorId = if (this[3].toString().isNullOrEmpty()) "1" else "0"
    val amount = if (this[3].toString().isNullOrEmpty()) this[4].toString().toDouble() else this[3].toString().toDouble()
    return Expense(
            comment = comment,
            date = date,
            isOffBudget = isOffBudget,
            author = Author(id = authorId, name = if (authorId == "0") "Лена" else "Макс", color = ""),
            amount = amount
    )
}