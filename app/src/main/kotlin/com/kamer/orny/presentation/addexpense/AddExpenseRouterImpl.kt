package com.kamer.orny.presentation.addexpense

import com.kamer.orny.data.android.ActivityHolder
import javax.inject.Inject


class AddExpenseRouterImpl @Inject constructor(
        val activityHolder: ActivityHolder
) : AddExpenseRouter {

    override fun closeScreen() {
        activityHolder.getActivity()?.finish()
    }
}