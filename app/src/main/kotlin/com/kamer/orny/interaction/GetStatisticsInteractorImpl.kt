package com.kamer.orny.interaction

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.model.Debt
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.interaction.model.UserStatistics
import io.reactivex.Observable


class GetStatisticsInteractorImpl(val pageRepo: PageRepo, val expenseRepo: ExpenseRepo) : GetStatisticsInteractor {

    override fun getStatistics(): Observable<Statistics> =
            Observable.just(Statistics(
                    usersStatistics = listOf(
                            UserStatistics("Max", 100.0, 200.0, 10.00),
                            UserStatistics("Lena", 100.0, 200.0, 10.00)
                    ),
                    debts = listOf(Debt("Max", "Lena", 10.0))
            ))
}