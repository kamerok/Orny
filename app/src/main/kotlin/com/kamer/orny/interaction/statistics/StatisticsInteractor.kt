package com.kamer.orny.interaction.statistics

import com.kamer.orny.interaction.model.Statistics
import io.reactivex.Observable


interface StatisticsInteractor {

    fun getStatistics(): Observable<Statistics>

}