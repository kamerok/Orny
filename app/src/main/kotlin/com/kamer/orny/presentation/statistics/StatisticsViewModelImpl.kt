package com.kamer.orny.presentation.statistics

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.interaction.GetStatisticsInteractor
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.utils.defaultBackgroundSchedulers
import timber.log.Timber


class StatisticsViewModelImpl(getStatisticsInteractor: GetStatisticsInteractor) : BaseViewModel(), StatisticsViewModel {

    private val loading = MutableLiveData<Boolean>()
    private val statistics = MutableLiveData<Statistics>()

    init {
        getStatisticsInteractor
                .getStatistics()
                .defaultBackgroundSchedulers()
                .disposeOnDestroy()
                .doOnSubscribe { loading.value = true }
                .doFinally { loading.value = false }
                .subscribe({
                    statistics.value = it
                },{
                    Timber.e(it)
                })
    }

    override fun bindShowLoading(): LiveData<Boolean> = loading

    override fun bindStatistics(): LiveData<Statistics> = statistics

}