package com.kamer.orny.presentation.shortcut

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.kamer.orny.R
import com.kamer.orny.presentation.editexpense.EditExpenseActivity


class ShortcutActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_CREATE_SHORTCUT == intent.action) {
            setupShortcut()
        }
        finish()
    }

    private fun setupShortcut() {
        val shortcutIntent = EditExpenseActivity.getIntent(this)
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val addIntent = Intent()
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.edit_expense_add_title))
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(applicationContext, R.mipmap.ic_add_expense))
        addIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
        applicationContext.sendBroadcast(addIntent)

        setResult(Activity.RESULT_OK, addIntent)
    }
}