package host.capitalquiz.wifiradioset.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.common.getExtra
import host.capitalquiz.wifiradioset.domain.CommunicationMode
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class WifiConnectionManager @Inject constructor(
    private val wifiManager: WifiP2pManager,
    @ApplicationContext private val context: Context,
    private val p2pConnectionChecker: NetworkChecker,
    private val communication: CommunicationMode,
) : BroadcastReceiver(), ConnectionManager {
    private var isRegistered = false
    private val channel = wifiManager.initialize(context, Looper.getMainLooper(), null)
    private val _wifiState = MutableStateFlow<WifiState>(WifiState.Idle)
    override val wifiState = _wifiState.asStateFlow()
    private var connectingDevice: WifiDevice? = null
    private var wifiEnabled = false
    private var wasConnected = false
    private var devices = listOf<WifiDevice>()

    init {
        Log.d("WifiConnectionManager", "communication in connMan: $communication")
    }

    override fun onReceive(context: Context?, intent: Intent) {
        Log.d("WifiConnectionManager", "onReceive: ${intent.action}")
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                wifiEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                _wifiState.update {
                    if (wifiEnabled) WifiState.On else WifiState.Off
                }
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                wifiManager.requestPeers(channel, peerListListener)
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val isConnected = p2pConnectionChecker.isConnected()
                val networkInfo = intent.getExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                Log.d(
                    "WifiConnectionManager",
                    "onReceive: CONNECTION_CHANGED_ACTION isConnected=$networkInfo isConnectedWithConnMan=$isConnected"
                )
                val device = intent.getExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                device?.let {
                    Log.d(
                        "WifiConnectionManager",
                        "onReceive: CONNECTION_CHANGED_ACTION device=${it.toWifiDevice()}"
                    )
                }
                if (networkInfo?.isConnected == true) {
                    wasConnected = true
                    wifiManager.requestConnectionInfo(channel, connectionListener)
                } else {
                    if (wifiEnabled && wasConnected) {
                        _wifiState.update { WifiState.Disconnected }
                        wasConnected = false
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device = intent.getExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                device?.let {
                    Log.d(
                        "WifiConnectionManager",
                        "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ${it.toWifiDevice()}"
                    )
                }
            }
        }
    }


    override fun init() {
        if (isRegistered.not()) {
            val intentFilter = IntentFilter().apply {
                addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION) // Indicates a change in the Wi-Fi Direct status.
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION) // Indicates a change in the list of available peers.
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION) // Indicates the state of Wi-Fi Direct connectivity has changed.
                addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION) // Indicates this device's details have changed.
            }
            context.registerReceiver(this, intentFilter)
            isRegistered = true
        }
    }

    override fun close() {
        if (isRegistered) {
            context.unregisterReceiver(this)
            isRegistered = false
        }
    }

    override fun connect(device: WifiDevice) {
        connectingDevice = device
        val config = WifiP2pConfig().apply {
            deviceAddress = device.address
            wps.setup = WpsInfo.PBC
        }

        wifiManager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                connectingDevice = null
                _wifiState.update { WifiState.ConnectionFailed }
            }
        })
    }

    override fun discoverDevices() {
        wifiManager.discoverPeers(channel, WifiDevicesDiscoveryListener())
    }

    override fun stopDiscoverDevices() {
        wifiManager.stopPeerDiscovery(channel, WifiDevicesDiscoveryListener())
    }

    fun interface Listener {
        fun onStateChanged(state: WifiState)
    }

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        devices = peerList.deviceList.map { it.toWifiDevice() }
        _wifiState.update { WifiState.DevicesFound(devices) }
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        // String from WifiP2pInfo struct
        val groupOwnerAddress = info.groupOwnerAddress
        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) { //Server
            Toast.makeText(context, "Server", Toast.LENGTH_SHORT).show()
            Log.d("WifiConnectionManager", "SERVER")
            communication.configureAsServer(connectingDevice ?: WifiDevice("Client", ""))
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) { // Client
            Toast.makeText(context, "Client", Toast.LENGTH_SHORT).show()
            Log.d("WifiConnectionManager1", "CLIENT devices=${devices}")
            Log.d("WifiConnectionManager1", "CLIENT hostName=${groupOwnerAddress}")
//            Log.d("WifiConnectionManager1", "CLIENT hostName=${groupOwnerAddress.hostName}")
            Log.d("WifiConnectionManager1", "CLIENT address=${groupOwnerAddress.address}")
//            Log.d("WifiConnectionManager1", "CLIENT canonicalHostName=${groupOwnerAddress.canonicalHostName}")
            Log.d("WifiConnectionManager1", "CLIENT hostAddress=${groupOwnerAddress.hostAddress}")

            communication.configureAsClient(
                connectingDevice ?: WifiDevice(
                    "Server",
                    groupOwnerAddress.hostAddress.takeOrEmpty()
                ), groupOwnerAddress
            )
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
        _wifiState.update { WifiState.Connected }
    }
}

private fun WifiP2pDevice.toWifiDevice(): WifiDevice = WifiDevice(deviceName, deviceAddress)
private fun String?.takeOrEmpty() = this ?: ""