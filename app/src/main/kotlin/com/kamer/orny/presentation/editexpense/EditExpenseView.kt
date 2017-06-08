package com.kamer.orny.presentation.editexpense

import android.content.Intent
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType


interface EditExpenseView : MvpView {

    fun startIntent(intent: Intent?)

}