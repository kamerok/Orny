package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.android.ActivityHolder


class EditExpenseRouterImpl(val activityHolder: ActivityHolder) : EditExpenseRouter {

    override fun closeScreen() {
        activityHolder.getActivity()?.finish()
    }
}