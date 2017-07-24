package com.kamer.orny.data.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kamer.orny.data.domain.model.Author


@Entity(tableName = "authors")
data class DbAuthor(
        @PrimaryKey val id: String,
        val position: Int,
        val name: String,
        val color: String
) {

    companion object {
        fun fromAuthor(author: Author) = DbAuthor(
                id = author.id,
                position = author.position,
                name = author.name,
                color = author.color
        )
    }

    fun toAuthor() = Author(
            id = id,
            position = position,
            name = name,
            color = color
    )

}