package com.kamer.orny.utils

import android.content.Context
import android.content.SharedPreferences
import com.kamer.orny.di.app.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class PrefsImpl @Inject constructor(context: Context) : Prefs {

    companion object {
        val ACCOUNT_NAME = "account_name"
    }

    val PREFS_FILENAME = "${context.packageName}.prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    override var accountName: String
        get() = prefs.getString(ACCOUNT_NAME, "")
        set(value) = prefs.edit().putString(ACCOUNT_NAME, value).apply()

    override fun clear() = prefs.edit().clear().apply()
}