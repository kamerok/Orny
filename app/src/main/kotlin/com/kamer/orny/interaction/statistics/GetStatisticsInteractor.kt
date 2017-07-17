package com.kamer.orny.interaction.statistics

import com.kamer.orny.interaction.model.Statistics
import io.reactivex.Observable


interface GetStatisticsInteractor {

    fun getStatistics(): Observable<Statistics>

}