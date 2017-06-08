package com.kamer.orny.presentation.editexpense

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.model.Author
import com.kamer.orny.presentation.core.MvpActivity
import com.kamer.orny.utils.onTextChanged
import com.kamer.orny.utils.setupToolbar
import kotlinx.android.synthetic.main.activity_edit_expense.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditExpenseActivity : MvpActivity(), EditExpenseView {

    @Inject lateinit var router: EditExpenseRouterImpl

    @InjectPresenter lateinit var presenter: EditExpensePresenter

    @ProvidePresenter
    fun providePresenter() = App.appComponent.presenterComponent().editExpensePresenter()

    companion object {

        fun getIntent(context: Context) = Intent(context, EditExpenseActivity::class.java)

        private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    private var authors = emptyList<Author>()
    private val adapter by lazy { ArrayAdapter<String>(this, android.R.layout.simple_spinner_item) }

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        router.setActivity(this)
        setContentView(R.layout.activity_edit_expense)
        setupToolbar(toolbarView)
        amountView.onTextChanged { presenter.amountChanged(it) }
        commentView.onTextChanged { presenter.commentChanged(it) }

        authorsSpinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.authorSelected(authors[position])
            }
        }
        authorsSpinnerView.adapter = adapter
        dateView.setOnClickListener { presenter.selectDate() }
        offBudgetView.setOnCheckedChangeListener { _, isChecked -> presenter.offBudgetChanged(isChecked) }

        bindViewModel()
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

    override fun showExitDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.edit_expense_exit_dialog_title)
                .setMessage(R.string.edit_expense_exit_dialog_message)
                .setPositiveButton(R.string.edit_expense_exit_dialog_save) { _, _ -> presenter.saveExpense() }
                .setNegativeButton(R.string.edit_expense_exit_dialog_exit) { _, _ -> presenter.confirmExit() }
                .show()
    }

    override fun showAmountError(error: String) {
        amountView.error = error
    }

    override fun showError(message: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
    }

    override fun startIntent(intent: Intent?) {
        startActivityForResult(intent, 1001)
    }

    private fun bindViewModel() {
        presenter.bindAuthors().subscribe { setAuthors(it) }
        presenter.bindDate().subscribe { setDate(it) }
        presenter.bindSavingProgress().subscribe { setSavingProgress(it) }
        presenter.bindShowDatePicker().subscribe { showDatePicker(it) }
    }

    private fun setAuthors(authors: List<Author>) {
        this.authors = authors
        adapter.clear()
        adapter.addAll(authors.map { it.name })
    }

    private fun setDate(date: Date) {
        dateView.text = DATE_FORMAT.format(date)
    }

    private fun setSavingProgress(isSaving: Boolean) {
        if (isSaving) {
            val dialog = ProgressDialog(this)
            dialog.setMessage(getString(R.string.edit_expense_exit_save_progress))
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            this.dialog = dialog
        } else {
            dialog?.dismiss()
        }
    }

    private fun showDatePicker(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date.time
        DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val newCalendar = Calendar.getInstance()
                    newCalendar.set(year, month, dayOfMonth)
                    val newDate = Date(newCalendar.timeInMillis)
                    setDate(newDate)
                    presenter.dateChanged(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
