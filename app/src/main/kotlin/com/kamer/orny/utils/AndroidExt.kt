package com.kamer.orny.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.*

fun Context.toast(message: CharSequence) =
        Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun Context.isDeviceOnline(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.hasPermission(permission: String): Boolean
        = packageManager.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED

fun AppCompatActivity.setupToolbar(toolbarView: Toolbar) {
    setSupportActionBar(toolbarView)
    val supportActionBar = supportActionBar
    if (supportActionBar != null) {
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setDisplayShowHomeEnabled(true)
    }
    toolbarView.setNavigationOnClickListener { _ -> onBackPressed() }
}

fun TextView.onTextChanged(listener: (String) -> Unit) = addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        listener.invoke(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
})

fun Calendar.dayStart() = this.apply {
    set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Date.dayStart(): Date = Calendar
        .getInstance()
        .apply {
            time = this@dayStart
            dayStart()
        }
        .time