package com.irfan.githubuser.util

import android.R.id.message
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import android.widget.Toast


object Commons {
    fun View.hide() {
        visibility = View.INVISIBLE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

}