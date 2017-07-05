package com.kamer.orny.interaction

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.model.Statistics
import io.reactivex.Observable


class GetStatisticsInteractorImpl(val pageRepo: PageRepo, val expenseRepo: ExpenseRepo) : GetStatisticsInteractor {

    override fun getStatistics(): Observable<Statistics> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}