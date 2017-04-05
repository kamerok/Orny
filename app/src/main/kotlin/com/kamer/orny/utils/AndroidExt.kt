package com.kamer.orny.utils

import android.content.Context
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