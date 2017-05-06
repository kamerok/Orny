package com.kamer.orny.presentation.editexpense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kamer.orny.R
import com.kamer.orny.presentation.MvpActivity
import com.kamer.orny.utils.toast

class EditExpenseActivity : MvpActivity(), EditExpenseView {

    @InjectPresenter lateinit var presenter: EditExpensePresenter

    companion object {
        fun getIntent(context: Context) = Intent(context, EditExpenseActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)
    }

    override fun showError(message: String) {
        toast(message)
    }
}
