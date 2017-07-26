package com.kamer.orny.data.room.query

import android.arch.persistence.room.Embedded
import com.kamer.orny.data.domain.model.Author


data class ExpenseEntryQuery(
        @Embedded val author: Author,
        val amount: Double
)