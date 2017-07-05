package com.kamer.orny.interaction

import com.kamer.orny.interaction.model.Statistics
import io.reactivex.Observable


interface GetStatisticsInteractor {

    fun getStatistics(): Observable<Statistics>

}