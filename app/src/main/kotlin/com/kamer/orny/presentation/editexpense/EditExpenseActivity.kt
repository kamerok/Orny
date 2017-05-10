package com.kamer.orny.presentation.editexpense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.model.Author
import com.kamer.orny.presentation.core.MvpActivity
import com.kamer.orny.utils.setupToolbar
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*
import javax.inject.Inject

class EditExpenseActivity : MvpActivity(), EditExpenseView {

    @Inject lateinit var router: EditExpenseRouterImpl

    @InjectPresenter lateinit var presenter: EditExpensePresenter

    @ProvidePresenter
    fun providePresenter() = App.appComponent.presenterComponent().editExpensePresenter()

    companion object {
        fun getIntent(context: Context) = Intent(context, EditExpenseActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        router.setActivity(this)
        setContentView(R.layout.activity_edit_expense)
        setupToolbar(toolbarView)
    }

    override fun onBackPressed() {
        presenter.exitScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_expense, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                presenter.saveExpense()
                return true
            }
            else -> return false
        }
    }

    override fun setAuthors(authors: List<Author>) {

    }

    override fun setDate(capture: Date) {

    }

    override fun setSavingProgress(isSaving: Boolean) {

    }

    override fun showExitDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.edit_expense_exit_dialog_title)
                .setMessage(R.string.edit_expense_exit_dialog_message)
                .setPositiveButton(R.string.edit_expense_exit_dialog_save) { _, _ -> presenter.saveExpense() }
                .setNegativeButton(R.string.edit_expense_exit_dialog_exit) { _, _ -> presenter.confirmExit() }
                .show()
    }

    override fun showAmountError(error: String) {

    }

    override fun showError(message: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
    }
}
