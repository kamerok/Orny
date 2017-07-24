package com.kamer.orny.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kamer.orny.data.room.entity.ExpenseEntity
import io.reactivex.Flowable


@Dao
interface ExpenseDao {

    @Query("DELETE FROM expenses")
    fun deleteAllExpenses()

    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flowable<List<ExpenseEntity>>

    @Insert
    fun insertAll(expenses: List<ExpenseEntity>)

    @Insert
    fun insert(expense: ExpenseEntity)

}