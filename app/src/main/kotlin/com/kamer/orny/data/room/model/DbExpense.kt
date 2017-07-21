package com.kamer.orny.data.room.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "expenses")
data class DbExpense(
       @PrimaryKey(autoGenerate = true) val id: Int = 0,
       val comment: String,
       val date: Date,
       val isOffBudget: Boolean,
       val values: List<Double>
)