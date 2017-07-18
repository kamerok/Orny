package com.kamer.orny.data.android

import android.content.Context
import android.content.SharedPreferences
import com.kamer.orny.di.app.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class PrefsImpl @Inject constructor(context: Context) : Prefs {

    companion object {
        const val ACCOUNT_NAME = "account_name"
        const val DEFAULT_AUTHOR_ID = "default_author_id"
    }

    val PREFS_FILENAME = "${context.packageName}.prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    override var accountName: String
        get() = prefs.getString(ACCOUNT_NAME, "")
        set(value) = prefs.edit().putString(ACCOUNT_NAME, value).apply()

    override var defaultAuthorId: String
        get() = prefs.getString(DEFAULT_AUTHOR_ID, "")
        set(value) = prefs.edit().putString(DEFAULT_AUTHOR_ID, value).apply()

    override fun clear() = prefs.edit().clear().apply()
}