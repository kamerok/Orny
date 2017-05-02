package com.kamer.orny.presentation.editexpense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kamer.orny.R

class EditExpenseActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, EditExpenseActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)
    }

}
