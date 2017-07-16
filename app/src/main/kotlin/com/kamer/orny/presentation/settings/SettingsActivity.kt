package com.kamer.orny.presentation.settings

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.kamer.orny.R
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.di.app.features.PageSettingsModule
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.utils.onTextChanged
import com.kamer.orny.utils.setVisible
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_settings.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class SettingsActivity : BaseActivity() {

    @field:[Inject Named(PageSettingsModule.PAGE_SETTINGS)]
    lateinit var pageSettingsViewModelFactory: ViewModelProvider.Factory

    private lateinit var pageSettingsViewModel: PageSettingsViewModel

    private var ignoreBudgetChange = false
    private var ignorePeriodChange = false

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun getIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        pageSettingsViewModel = ViewModelProviders.of(this, pageSettingsViewModelFactory).get(PageSettingsViewModelImpl::class.java)
        initViews()
        bindViewModels()
    }

    private fun initViews() {
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
    }

    private fun bindViewModels() {
        pageSettingsViewModel.bindFieldsEditable().observe(this, Observer { if (it != null) setFieldsEditable(it) })
        pageSettingsViewModel.bindSaveButtonEnabled().observe(this, Observer { if (it != null) setSaveEnabled(it) })
        pageSettingsViewModel.bindLoadingProgress().observe(this, Observer { if (it != null) setLoadingProgress(it) })
        pageSettingsViewModel.bindSavingProgress().observe(this, Observer { if (it != null) setSavingProgress(it) })
        pageSettingsViewModel.bindPageSettings().observe(this, Observer { if (it != null) updateSettings(it) })
        pageSettingsViewModel.bindShowDatePicker().observe(this, Observer { if (it != null) showDatePicker(it) })
        pageSettingsViewModel.bindError().observe(this, Observer { if (it != null) showError(it) })
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

}
