package com.kamer.orny.data.room

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<Double> {
        return value?.split(",")?.map { it.toDouble() } ?: emptyList()
    }

    @TypeConverter
    fun dateToTimestamp(list: List<Double>?): String? {
        return list?.joinToString(separator = ",", transform = { it.toString() })
    }
}