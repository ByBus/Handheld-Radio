package host.capitalquiz.wifiradioset.data

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import javax.inject.Inject

interface NetworkChecker {
    fun isConnected(): Boolean

    class WiFiChecker @Inject constructor(
        private val connectivityManager: ConnectivityManager,
    ) : NetworkChecker {
        private var isConnected = false

        private val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("WiFiChecker", "onLost: ")
                isConnected = false
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Log.d("WiFiChecker", "onUnavailable: ")
                isConnected = false
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("WiFiChecker", "onAvailable: ")
                isConnected = true
            }
        }

        init {
            Log.d("WiFiChecker", "INIT: ")
            connectivityManager.requestNetwork(networkRequest, networkCallback)
        }

        override fun isConnected(): Boolean = isConnected

    }
}