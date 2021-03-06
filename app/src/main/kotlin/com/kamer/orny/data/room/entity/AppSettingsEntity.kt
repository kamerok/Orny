package com.kamer.orny.data.room.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(
        tableName = "app_settings",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = AuthorEntity::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("default_author_id"),
                        deferred = true
                )
        )
)
data class AppSettingsEntity(
        @PrimaryKey val id: Int = 0,
        @ColumnInfo(name = "default_author_id", index = true) val defaultAuthorId: String?
)