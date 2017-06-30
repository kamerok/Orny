package com.kamer.orny.data.google.model

import android.text.format.DateUtils
import com.google.api.services.sheets.v4.model.CellData
import com.google.api.services.sheets.v4.model.CellFormat
import com.google.api.services.sheets.v4.model.ExtendedValue
import com.google.api.services.sheets.v4.model.NumberFormat
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*


data class GoogleExpense(val comment: String?, val date: Date?, val isOffBudget: Boolean, val values: List<Double>) {

    companion object {
        val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

}

fun GoogleExpense.toCells(): MutableList<CellData> {
    val cellsData: MutableList<CellData> = mutableListOf()
    cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setStringValue(comment ?: "") })
    if (date == null) {
        cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setStringValue("") })
    } else {
        val startCalendar = Calendar.getInstance().apply { set(1899, 12, 30) }
        val current = Calendar.getInstance().apply { timeInMillis = date.time }
        val days: Double = ((current.timeInMillis - startCalendar.timeInMillis) / DateUtils.DAY_IN_MILLIS).toDouble() + 1
        cellsData.add(CellData().apply {
            userEnteredValue = ExtendedValue().setNumberValue(days)
            userEnteredFormat = CellFormat().setNumberFormat(NumberFormat().setType("DATE"))
        })
    }
    cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setNumberValue(if (isOffBudget) 1.0 else 0.0) })
    values.forEach { value ->
        if (value == 0.0) {
            cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setStringValue("") })
        } else {
            cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setNumberValue(value) })
        }
    }
    return cellsData
}

fun MutableList<Any>.toExpense(): GoogleExpense {
    val comment = this[0].toString()
    val date = try {
        GoogleExpense.DATE_FORMAT.parse(this[1].toString())
    } catch (e: IllegalArgumentException) {
        null
    }
    val isOffBudget = this[2].toString() == "1"
    val values = this
            .takeLast(size - 3)
            .map { it.toString() }
            .map { it.toDoubleOrNull() ?: 0.0 }
    return GoogleExpense(
            comment = comment,
            date = date,
            isOffBudget = isOffBudget,
            values = values
    )
}