package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject


@ApplicationScope
class GooglePageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo
) : GooglePageRepo {

    private val pageSubject = BehaviorSubject.create<GooglePage>()

    private val updateCompletable by lazy {
        googleRepo
                .getPage()
                .toObservable()
                .share()
                .firstOrError()
                .doOnSuccess { pageSubject.onNext(it) }
                .toCompletable()
    }

    override fun getPage(): Observable<GooglePage> =
            if (pageSubject.hasValue()) {
                pageSubject
            } else {
                updateCompletable
                        .andThen(pageSubject)
            }

    override fun updatePage(): Completable = updateCompletable

    override fun addExpense(expense: GoogleExpense): Completable =
            googleRepo
                    .addExpense(expense)
                    .andThen(pageSubject)
                    .firstElement()
                    .flatMapCompletable {
                        Completable
                                .fromAction {
                                    pageSubject.onNext(it.copy(expenses = it.expenses.plus(expense)))
                                }
                    }

    override fun savePageSettings(budget: Double, startDate: Date, period: Int): Completable =
            googleRepo
                    .savePageSettings(budget, startDate, period)
                    .andThen(pageSubject)
                    .firstElement()
                    .flatMapCompletable {
                        Completable
                                .fromAction {
                                    pageSubject.onNext(it.copy(
                                            budget = budget,
                                            startDate = startDate,
                                            periodDays = period
                                    ))
                                }
                    }

}