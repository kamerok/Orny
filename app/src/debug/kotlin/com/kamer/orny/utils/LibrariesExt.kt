package com.kamer.orny.utils

import android.content.Context
import com.facebook.stetho.Stetho


fun Context.installStetho() = Stetho.initializeWithDefaults(this)