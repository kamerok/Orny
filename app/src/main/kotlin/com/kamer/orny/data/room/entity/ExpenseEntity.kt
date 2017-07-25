package com.kamer.orny.data.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "expenses")
data class ExpenseEntity(
        @PrimaryKey val id: String,
        val comment: String,
        val date: Date,
        @ColumnInfo(name = "is_off_budget") val isOffBudget: Boolean
)