package com.kamer.orny.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast

fun Context.toast(message: CharSequence) =
        Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
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