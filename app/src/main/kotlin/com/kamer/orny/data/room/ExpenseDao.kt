package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
import com.kamer.orny.data.room.query.ExpenseQuery
import io.reactivex.Flowable


@Dao
interface ExpenseDao {

    @Query("DELETE FROM expenses")
    fun deleteAllExpenses()

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flowable<List<ExpenseQuery>>

    @Insert
    fun insertAll(expenses: List<ExpenseEntity>)

    @Insert
    fun insert(expense: ExpenseEntity)

    @Insert
    fun insertAllEntries(expenses: List<ExpenseEntryEntity>)

    @Insert
    fun insertEntry(expense: ExpenseEntryEntity)

}