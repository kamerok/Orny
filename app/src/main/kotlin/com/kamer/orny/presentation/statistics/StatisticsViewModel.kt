package com.kamer.orny.presentation.statistics

import android.arch.lifecycle.LiveData
import com.kamer.orny.interaction.model.Statistics


interface StatisticsViewModel {

    val statisticsStream: LiveData<Statistics>

}