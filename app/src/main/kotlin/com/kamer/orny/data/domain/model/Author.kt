package com.kamer.orny.data.domain.model


data class Author(
        val id: String,
        val position: Int,
        val name: String,
        val color: String
) {
    companion object {
        val EMPTY_AUTHOR = Author("", 0, "", "")
    }
}