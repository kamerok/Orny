package com.kamer.orny.presentation.settings

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.kamer.orny.R
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.core.VMProvider
import com.kamer.orny.utils.onTextChanged
import com.kamer.orny.utils.safeObserve
import com.kamer.orny.utils.setVisible
import com.kamer.orny.utils.setupToolbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@JvmSuppressWildcards
class SettingsActivity : BaseActivity() {

    @Inject lateinit var pageSettingsViewModelProvider: VMProvider<PageSettingsViewModel>
    @Inject lateinit var appSettingsViewModelProvider: VMProvider<AppSettingsViewModel>

    private val pageSettingsViewModel: PageSettingsViewModel by lazy { pageSettingsViewModelProvider.get(this) }
    private val appSettingsViewModel: AppSettingsViewModel by lazy { appSettingsViewModelProvider.get(this) }

    private var ignoreBudgetChange = false
    private var ignorePeriodChange = false

    private var authors = emptyList<Author>()
    private val adapter by lazy { ArrayAdapter<String>(this, R.layout.item_edit_expense_author) }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun getIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initViews()
        bindViewModels()
    }

    private fun initViews() {
        setupToolbar(toolbarView)
        budgetView.onTextChanged {
            if (ignoreBudgetChange) ignoreBudgetChange = false
            else pageSettingsViewModel.budgetChanged(it)
        }
        dateView.setOnClickListener { pageSettingsViewModel.selectDate() }
        periodView.onTextChanged {
            if (ignorePeriodChange) ignorePeriodChange = false
            else pageSettingsViewModel.periodChanged(it)
        }
        saveButton.setOnClickListener { pageSettingsViewModel.saveSettings() }
        authorsSpinnerView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                appSettingsViewModel.authorSelected(authors[position])
            }
        }
        authorsSpinnerView.adapter = adapter
    }

    private fun bindViewModels() {
        pageSettingsViewModel.fieldsEditableStream.safeObserve(this, this::setFieldsEditable)
        pageSettingsViewModel.saveButtonEnabledStream.safeObserve(this, this::setSaveEnabled)
        pageSettingsViewModel.loadingProgressStream.safeObserve(this, this::setLoadingProgress)
        pageSettingsViewModel.savingProgressStream.safeObserve(this, this::setSavingProgress)
        pageSettingsViewModel.pageSettingsStream.safeObserve(this, this::updateSettings)
        pageSettingsViewModel.showDatePickerStream.safeObserve(this, this::showDatePicker)
        pageSettingsViewModel.errorStream.safeObserve(this, this::showError)

        appSettingsViewModel.modelStream.safeObserve(this, this::updateAuthors)
        appSettingsViewModel.errorStream.safeObserve(this, this::showError)
    }

    private fun setFieldsEditable(isEditable: Boolean) {
        budgetView.isEnabled = isEditable
        dateView.isEnabled = isEditable
        periodView.isEnabled = isEditable
    }

    private fun setSaveEnabled(isEnabled: Boolean) {
        saveButton.isEnabled = isEnabled
    }

    private fun setLoadingProgress(isLoading: Boolean) {
        loadingProgressView.setVisible(isLoading)
    }

    private fun setSavingProgress(isSaving: Boolean) {
        saveProgressView.setVisible(isSaving)
    }

    private fun updateSettings(settings: PageSettings) {
        ignoreBudgetChange = true
        budgetView.setText(settings.budget.toString())
        dateView.text = DATE_FORMAT.format(settings.startDate)
        ignorePeriodChange = true
        periodView.setText(settings.period.toString())
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
                    pageSettingsViewModel.startDateChanged(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .show()
    }

    private fun updateAuthors(authorsWithDefault: AuthorsWithDefault) {
        authors = authorsWithDefault.authors
        adapter.clear()
        adapter.addAll(authorsWithDefault.authors.map { it.name })
        authorsSpinnerView.setSelection(authorsWithDefault.selectedIndex)
    }

}
