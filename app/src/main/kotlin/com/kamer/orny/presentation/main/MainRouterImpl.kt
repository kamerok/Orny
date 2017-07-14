package com.kamer.orny.presentation.main

import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.presentation.settings.SettingsActivity
import javax.inject.Inject


class MainRouterImpl @Inject constructor(
        val activityHolder: ActivityHolder
) : MainRouter {

    override fun openAddExpenseScreen() {
        activityHolder.getActivity()?.run { startActivity(EditExpenseActivity.getIntent(this)) }
    }

    override fun openSettingsScreen() {
        activityHolder.getActivity()?.run { startActivity(SettingsActivity.getIntent(this)) }
    }
}