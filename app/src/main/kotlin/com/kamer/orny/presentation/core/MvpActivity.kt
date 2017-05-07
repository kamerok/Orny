package com.kamer.orny.presentation.core

abstract class MvpActivity : BaseActivity() {

    private val mvpDelegate: com.arellomobile.mvp.MvpDelegate<out MvpActivity> by lazy {
        com.arellomobile.mvp.MvpDelegate(this)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
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

    override fun onSaveInstanceState(outState: android.os.Bundle) {
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