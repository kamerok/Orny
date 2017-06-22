package com.kamer.orny.presentation.main

import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import com.kamer.orny.utils.defaultBackgroundSchedulers
import com.kamer.orny.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject lateinit var googleRepo: GoogleRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContentView(R.layout.activity_main)
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

}
