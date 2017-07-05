package com.kamer.orny.presentation.statistics

import android.arch.lifecycle.LiveData
import com.kamer.orny.interaction.model.Statistics


interface StatisticsViewModel {

    fun bindShowLoading(): LiveData<Boolean>

    fun bindStatistics(): LiveData<Statistics>

}