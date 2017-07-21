package com.kamer.orny.data.room.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "authors")
data class DbAuthor(
        @PrimaryKey val id: String,
        val position: Int,
        val name: String,
        val color: String
)