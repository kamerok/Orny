package com.kamer.orny.data.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "app_settings")
data class DbAppSettings(
        @PrimaryKey val id: Int = 0,
        @ColumnInfo(name = "default_author_id") val defaultAuthorId: String
)