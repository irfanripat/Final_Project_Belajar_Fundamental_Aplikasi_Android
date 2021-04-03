package com.irfan.githubuser.util

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import com.irfan.githubuser.R

class TimePickerFragment internal constructor(
    private val onTimeSetListener: OnTimeSetListener,
    private val hours: Int,
    private val minutes: Int
) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(
            activity, R.style.dateTimePicker,
            onTimeSetListener, hours, minutes, DateFormat.is24HourFormat(
                activity
            )
        )
    }
}