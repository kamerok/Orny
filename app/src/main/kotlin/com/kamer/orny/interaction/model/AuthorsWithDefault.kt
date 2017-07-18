package com.kamer.orny.interaction.model

import com.kamer.orny.data.domain.model.Author


data class AuthorsWithDefault(
        val selectedAuthor: Author = Author.EMPTY_AUTHOR,
        val authors: List<Author> = emptyList()
) {
    val selectedIndex = authors.indexOf(selectedAuthor)
}