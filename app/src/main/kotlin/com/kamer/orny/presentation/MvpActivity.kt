package com.kamer.orny.presentation

import android.os.Bundle

import com.arellomobile.mvp.MvpDelegate

abstract class MvpActivity : BaseActivity() {

    private val mvpDelegate: MvpDelegate<out MvpActivity> by lazy {
        MvpDelegate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mvpDelegate.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        mvpDelegate.onAttach()
    }

    override fun onResume() {
        super.onResume()

        mvpDelegate.onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mvpDelegate.onSaveInstanceState(outState)
        mvpDelegate.onDetach()
    }

    override fun onStop() {
        super.onStop()

        mvpDelegate.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            mvpDelegate.onDestroy()
        }
    }

}