package com.kamer.orny.presentation.statistics

import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


class StatisticsViewModelImpl : BaseViewModel(), StatisticsViewModel {

    private val loading = BehaviorSubject.create<Boolean>()

    init {
        Completable
                .fromAction {
                    Thread.sleep(3000)
                }
                .defaultBackgroundSchedulers()
                .disposeOnDestroy()
                .doOnSubscribe { loading.onNext(true) }
                .doFinally { loading.onNext(false) }
                .subscribe()
    }

    override fun bindShowLoading(): Observable<Boolean> = loading

}