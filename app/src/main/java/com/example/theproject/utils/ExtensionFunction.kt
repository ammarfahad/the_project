package com.example.theproject.utils

import android.content.Context
import android.widget.Toast

/**
 * The toast message function to show the toast, by default the duration is short.
 * To show long duration pass the longDuration parameter as true
 * */
fun Context.toast(message: String, longDuration: Boolean = false) {
    val toast = if (longDuration) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
    } else {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }
    toast.show()
}