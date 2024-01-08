package com.example.arrivalcharm.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

interface InternetConnectionCallback {
    fun onConnected()
    fun onDisconnected()
}

object DoesNetworkHaveInternet {
    val TAG = this.javaClass.name
    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try{
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        }catch (e: IOException){
            false
        }
    }
}

object InternetConnectionObserver {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var cm: ConnectivityManager? = null
    private val validNetworks: MutableSet<Network> = HashSet()
    private var connectionCallback: InternetConnectionCallback? = null

    fun instance(context: Context): InternetConnectionObserver {
        cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return this
    }

    fun setCallback(connectionCallback: InternetConnectionCallback): InternetConnectionObserver {
        this.connectionCallback = connectionCallback
        return this
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = cm?.getNetworkCapabilities(network)
            val hasInternetCapability =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (hasInternetCapability == true) {
                // check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }
        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }

    }

    private fun checkValidNetworks() {
        val status = validNetworks.size > 0
        if (status) {
            connectionCallback?.onConnected()
        } else {
            connectionCallback?.onDisconnected()
        }
    }

    fun register(): InternetConnectionObserver {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm?.registerNetworkCallback(networkRequest, networkCallback)
        return this
    }

    fun unRegister() {
        cm?.unregisterNetworkCallback(networkCallback)
    }

}