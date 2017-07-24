package com.kamer.orny.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.kamer.orny.data.room.entity.AppSettingsEntity
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.PageSettingsEntity


@Database(entities = arrayOf(
        ExpenseEntity::class,
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