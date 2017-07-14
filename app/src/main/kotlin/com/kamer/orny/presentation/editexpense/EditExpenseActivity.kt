package com.kamer.orny.presentation.editexpense

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.kamer.orny.R
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.utils.onTextChanged
import com.kamer.orny.utils.setupToolbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_edit_expense.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditExpenseActivity : BaseActivity(), LifecycleOwner {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun getIntent(context: Context) = Intent(context, EditExpenseActivity::class.java)
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: EditExpenseViewModel

    private var authors = emptyList<Author>()
    private val adapter by lazy { ArrayAdapter<String>(this, R.layout.item_edit_expense_author) }

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditExpenseViewModelImpl::class.java)
        initViews()
        bindViewModel()
    }

    override fun onBackPressed() {
        viewModel.exitScreen()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_expense, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                viewModel.saveExpense()
                return true
            }
            else -> return false
        }
    }

    private fun initViews() {
        setupToolbar(toolbarView)
        amountView.onTextChanged { viewModel.amountChanged(it) }
        commentView.onTextChanged { viewModel.commentChanged(it) }
        commentView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.saveExpense()
                    true
                }
                else -> false
            }
        }

        authorsSpinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.authorSelected(authors[position])
            }
        }
        authorsSpinnerView.adapter = adapter
        changeAuthorView.setOnClickListener {
            authorsSpinnerView.apply {
                var newSelected = selectedItemPosition + 1
                if (newSelected >= count) {
                    newSelected = 0
                }
                setSelection(newSelected)
            }
        }
        dateView.setOnClickListener { viewModel.selectDate() }
        offBudgetView.setOnCheckedChangeListener { _, isChecked -> viewModel.offBudgetChanged(isChecked) }
    }

    private fun bindViewModel() {
        viewModel.bindAuthors().observe(this, Observer { if (it != null) setAuthors(it) })
        viewModel.bindDate().observe(this, Observer { if (it != null) setDate(it) })
        viewModel.bindSavingProgress().observe(this, Observer { if (it != null) setSavingProgress(it) })
        viewModel.bindShowDatePicker().observe(this, Observer { if (it != null) showDatePicker(it) })
        viewModel.bindShowExitDialog().observe(this, Observer { showExitDialog() })
        viewModel.bindShowAmountError().observe(this, Observer { if (it != null) showAmountError(it) })
        viewModel.bindShowError().observe(this, Observer { if (it != null) showError(it) })
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
                    viewModel.dateChanged(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.edit_expense_exit_dialog_title)
                .setMessage(R.string.edit_expense_exit_dialog_message)
                .setPositiveButton(R.string.edit_expense_exit_dialog_save) { _, _ -> viewModel.saveExpense() }
                .setNegativeButton(R.string.edit_expense_exit_dialog_exit) { _, _ -> viewModel.confirmExit() }
                .show()
    }

    private fun showAmountError(error: String) {
        amountView.error = error
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
    }
}
