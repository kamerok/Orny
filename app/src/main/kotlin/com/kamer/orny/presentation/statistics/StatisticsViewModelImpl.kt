package com.kamer.orny.presentation.statistics

import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.interaction.statistics.StatisticsInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import timber.log.Timber
import javax.inject.Inject


class StatisticsViewModelImpl @Inject constructor(
        interactor: StatisticsInteractor
) : BaseViewModel(), StatisticsViewModel {

    override val statisticsStream = MutableLiveData<Statistics>()

    init {
        interactor
                .getStatistics()
                .disposeOnDestroy()
                .subscribe({
                    statisticsStream.value = it
                }, {
                    Timber.e(it)
                })
    }

}