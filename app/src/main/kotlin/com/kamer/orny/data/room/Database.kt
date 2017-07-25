package com.kamer.orny.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.kamer.orny.data.room.entity.*


@Database(entities = arrayOf(
        ExpenseEntity::class,
        ExpenseEntryEntity::class,
        AuthorEntity::class,
        PageSettingsEntity::class,
        AppSettingsEntity::class
), version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun authorDao(): AuthorDao

    abstract fun settingsDao(): SettingsDao

}