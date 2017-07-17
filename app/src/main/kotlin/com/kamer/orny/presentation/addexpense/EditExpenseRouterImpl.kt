package com.kamer.orny.presentation.addexpense

import com.kamer.orny.data.android.ActivityHolder
import javax.inject.Inject


class EditExpenseRouterImpl @Inject constructor(
        val activityHolder: ActivityHolder
) : EditExpenseRouter {

    override fun closeScreen() {
        activityHolder.getActivity()?.finish()
    }
}