package com.kamer.orny.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.presentation.core.BaseActivity
import dagger.android.AndroidInjection


class SettingsActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_settings)
    }

}