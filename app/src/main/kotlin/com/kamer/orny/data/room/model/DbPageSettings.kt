package com.kamer.orny.data.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "page_settings")
data class DbPageSettings(
        @PrimaryKey val id: Int = 0,
        val budget: Double,
        @ColumnInfo(name = "start_date") val startDate: Date,
        val period: Int
)