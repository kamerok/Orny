package com.kamer.orny.presentation.core

import android.support.v4.app.FragmentActivity


interface VMProvider<out VM> {

    fun get(fragmentActivity: FragmentActivity): VM

}