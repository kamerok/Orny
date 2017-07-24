package com.kamer.orny.data.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kamer.orny.data.domain.model.PageSettings
import java.util.*


@Entity(tableName = "page_settings")
data class DbPageSettings(
        @PrimaryKey val id: Int = 0,
        val budget: Double,
        @ColumnInfo(name = "start_date") val startDate: Date,
        val period: Int
) {
    companion object {
        fun fromPageSettings(settings: PageSettings) = DbPageSettings(
                budget = settings.budget,
                startDate = settings.startDate,
                period = settings.period
        )
    }

    fun toPageSettings() = PageSettings(
            budget = budget,
            startDate = startDate,
            period = period
    )
}