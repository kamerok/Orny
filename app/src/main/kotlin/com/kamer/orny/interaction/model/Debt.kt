package com.kamer.orny.interaction.model

import com.kamer.orny.data.domain.model.Author


data class Debt(
        val from: Author,
        val to: Author,
        val amount: Double
)