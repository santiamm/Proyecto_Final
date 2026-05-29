package com.example.proyecto_final.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (isNetworkAvailable(context)) {
            onNetworkConnected?.invoke(context)
        } else {
            onNetworkDisconnected?.invoke()
        }
    }

    companion object {

        var onNetworkConnected: ((Context) -> Unit)? = null

        var onNetworkDisconnected: (() -> Unit)? = null

        fun isNetworkAvailable(context: Context): Boolean {

            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE)
                        as ConnectivityManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val network = cm.activeNetwork ?: return false

                val capabilities =
                    cm.getNetworkCapabilities(network)
                        ?: return false

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

            } else {

                val info = cm.activeNetworkInfo
                info != null && info.isConnected
            }
        }
    }
}