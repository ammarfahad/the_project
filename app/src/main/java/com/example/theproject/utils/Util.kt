package com.example.theproject.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object Util {
    /**
     * The function is for logging. To use the specific type of log pass the true in the arguments
     * by default debugLog is going to come and tag is by default "defaultLOG"
     * @param tag : String
     * @param message : String
     * @param verboseLog : Boolean
     * @param debugLog : Boolean
     * @param infoLog : Boolean
     * @param warningLog : Boolean
     * @param errorLog : Boolean
     * */
    fun log(
        tag : String = "defaultLOG",
        message : String,
        verboseLog : Boolean = false,
        debugLog : Boolean = false,
        infoLog : Boolean = false,
        warningLog : Boolean = false,
        errorLog : Boolean = false
    ){
        when {
            verboseLog -> Log.v(tag, message)
            debugLog -> Log.d(tag, message)
            infoLog -> Log.i(tag, message)
            warningLog -> Log.w(tag, message)
            errorLog -> Log.e(tag, message)
            else -> Log.d(tag, message)
        }
    }

    /** The function is for checking internet connectivity */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}