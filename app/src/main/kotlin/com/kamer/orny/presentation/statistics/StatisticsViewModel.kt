package com.kamer.orny.presentation.statistics

import io.reactivex.Observable


interface StatisticsViewModel {

    fun bindShowLoading(): Observable<Boolean>

}