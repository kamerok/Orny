package com.kamer.orny.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.kamer.orny.data.room.model.DbAuthor
import com.kamer.orny.data.room.model.DbExpense
import com.kamer.orny.data.room.model.DbPageSettings


@Database(entities = arrayOf(DbExpense::class, DbAuthor::class, DbPageSettings::class), version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun authorDao(): AuthorDao

    abstract fun settingsDao(): SettingsDao

}