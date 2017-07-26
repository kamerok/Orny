package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
import com.kamer.orny.data.room.query.ExpenseWithEntities
import io.reactivex.Flowable


@Dao
interface ExpenseDao {

    @Query("DELETE FROM expenses")
    fun deleteAllExpenses()

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flowable<List<ExpenseWithEntities>>

    @Insert
    fun insertAll(expenses: List<ExpenseEntity>)

    @Insert
    fun insert(expense: ExpenseEntity)

    @Query("DELETE FROM expenses_entries")
    fun deleteAllEntries()

    @Insert
    fun insertAllEntries(entries: List<ExpenseEntryEntity>)

    @Insert
    fun insertEntry(entry: ExpenseEntryEntity)

}