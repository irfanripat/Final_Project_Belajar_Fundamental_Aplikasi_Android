package com.irfan.githubuser.util

import android.content.Context
import android.view.View
import android.widget.Toast


object Commons {
    fun View.hide() {
        visibility = View.INVISIBLE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun showViews(vararg views: View) {
        views.forEach {
            it.show()
        }
    }

    fun hideViews(vararg views: View) {
        views.forEach {
            it.hide()
        }
    }

    fun Context.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}