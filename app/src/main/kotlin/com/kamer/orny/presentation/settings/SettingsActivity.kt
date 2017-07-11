package com.kamer.orny.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.app.App
import com.kamer.orny.presentation.core.BaseActivity


class SettingsActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        setContentView(R.layout.activity_settings)
    }

}