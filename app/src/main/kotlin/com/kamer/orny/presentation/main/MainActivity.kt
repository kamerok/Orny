package com.kamer.orny.presentation.main

import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.editexpense.EditExpenseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject lateinit var googleRepo: GoogleRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContentView(R.layout.activity_main)
        buttonView.setOnClickListener { startActivity(EditExpenseActivity.getIntent(this)) }
    }

    override fun onResume() {
        super.onResume()
        googleRepo
                .getAllExpenses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .disposeOnDestroy()
                .subscribe { list ->
                    textView.text = list.size.toString()
                }
    }

}
