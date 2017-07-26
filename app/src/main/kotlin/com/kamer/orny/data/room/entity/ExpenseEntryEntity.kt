package com.kamer.orny.data.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(
        tableName = "expenses_entries",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = AuthorEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("author_id"),
                        onDelete = ForeignKey.CASCADE,
                        deferred = true
                ),
                ForeignKey(
                        entity = ExpenseEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("expense_id"),
                        onDelete = ForeignKey.CASCADE,
                        deferred = true
                )
        )
)
data class ExpenseEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "author_id", index = true) val authorId: String,
    @ColumnInfo(name = "expense_id", index = true) val expenseId: String,
    val amount: Double
)