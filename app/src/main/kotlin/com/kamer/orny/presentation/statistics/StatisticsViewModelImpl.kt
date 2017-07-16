package com.kamer.orny.presentation.statistics

import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.interaction.GetStatisticsInteractor
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.utils.defaultBackgroundSchedulers
import timber.log.Timber
import javax.inject.Inject


class StatisticsViewModelImpl @Inject constructor(
        getStatisticsInteractor: GetStatisticsInteractor
) : BaseViewModel(), StatisticsViewModel {

    override val showLoadingStream = MutableLiveData<Boolean>()
    override val statisticsStream = MutableLiveData<Statistics>()

    init {
        getStatisticsInteractor
                .getStatistics()
                .defaultBackgroundSchedulers()
                .disposeOnDestroy()
                .doOnSubscribe { showLoadingStream.value = true }
                .doFinally { showLoadingStream.value = false }
                .subscribe({
                    statisticsStream.value = it
                }, {
                    Timber.e(it)
                })
    }

}