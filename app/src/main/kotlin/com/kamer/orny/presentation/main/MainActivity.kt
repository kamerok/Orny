package com.kamer.orny.presentation.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.di.app.ViewModelModule
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.presentation.statistics.StatisticsViewModel
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import com.kamer.orny.utils.defaultBackgroundSchedulers
import com.kamer.orny.utils.gone
import com.kamer.orny.utils.toast
import com.kamer.orny.utils.visible
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named


class MainActivity : BaseActivity() {

    @Inject lateinit var googleRepo: GoogleRepo

    @field:[Inject Named(ViewModelModule.STATISTICS)] lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StatisticsViewModelImpl::class.java)
        initViews()
        bindViewModels()
    }

    private fun initViews() {
        loadButtonView.setOnClickListener {
            googleRepo
                    .getAllExpenses()
                    .disposeOnDestroy()
                    .defaultBackgroundSchedulers()
                    .subscribe({ list ->
                        textView.text = list.size.toString()
                    }, {
                        toast(it.message.toString())
                    })
        }
        openButtonView.setOnClickListener { startActivity(EditExpenseActivity.getIntent(this)) }
    }

    private fun bindViewModels() {
        viewModel.bindShowLoading().disposeOnDestroy().subscribe(this::setLoading)
    }

    private fun setLoading(isLoading: Boolean) = loadingProgressView.run { if (isLoading) visible() else gone() }

}
