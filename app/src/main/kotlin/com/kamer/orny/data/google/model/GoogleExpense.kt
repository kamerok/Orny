package com.kamer.orny.data.google.model

import android.text.format.DateUtils
import com.google.api.services.sheets.v4.model.CellData
import com.google.api.services.sheets.v4.model.CellFormat
import com.google.api.services.sheets.v4.model.ExtendedValue
import com.google.api.services.sheets.v4.model.NumberFormat
import com.kamer.orny.data.google.GoogleRepoImpl
import com.kamer.orny.utils.dayStart
import java.lang.IllegalArgumentException
import java.util.*


data class GoogleExpense(
        val comment: String?,
        val date: Date?,
        val isOffBudget: Boolean,
        val values: List<Double>
) {

    companion object {
        fun fromList(list: MutableList<Any>): GoogleExpense {
            val comment = list[0].toString()
            val date = try {
                GoogleRepoImpl.DATE_FORMAT.parse(list[1].toString())
            } catch (e: IllegalArgumentException) {
                null
            }
            val isOffBudget = list[2].toString() == "1"
            val values = list
                    .takeLast(list.size - 3)
                    .map { it.toString() }
                    .map { it.toDoubleOrNull() ?: 0.0 }
            return GoogleExpense(
                    comment = comment,
                    date = date,
                    isOffBudget = isOffBudget,
                    values = values
            )
        }
    }

    fun toCells(): MutableList<CellData> {
        val cellsData: MutableList<CellData> = mutableListOf()
        cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setStringValue(comment ?: "") })
        if (date == null) {
            cellsData.add(CellData().apply { userEnteredValue = ExtendedValue().setStringValue("") })
        } else {
            val startCalendar = Calendar.getInstance().apply { set(1899, Calendar.DECEMBER, 29) }
            val current = Calendar.getInstance().apply { timeInMillis = date.time }
            val days: Double = daysBetween(startCalendar, current).toDouble()
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

    private fun daysBetween(c1: Calendar, c2: Calendar): Int {
        val day1 = c1.dayStart().timeInMillis / DateUtils.DAY_IN_MILLIS
        val day2 = c2.dayStart().timeInMillis / DateUtils.DAY_IN_MILLIS
        return (day2 - day1).toInt()
    }

}