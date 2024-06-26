package host.capitalquiz.wifiradioset.data.devices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.os.Looper
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
    private val communication: CommunicationMode,
) : BroadcastReceiver(), ConnectionManager {
    private var isRegistered = false
    private val channel = wifiManager.initialize(context, Looper.getMainLooper(), null)
    private val _wifiState = MutableStateFlow<WifiState>(WifiState.Idle)
    override val state = _wifiState.asStateFlow()
    private var connectedDevice: WifiDevice? = null
    private var wifiEnabled = false
    private var wasConnected = false
    private var devices = listOf<WifiDevice>()

    override fun onReceive(context: Context?, intent: Intent) {
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
                val networkInfo = intent.getExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                val p2pGroup = intent.getExtra<WifiP2pGroup>(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)

                if (networkInfo?.isConnected == true) {
                    p2pGroup?.let { group ->
                        connectedDevice = if (group.isGroupOwner) {
                            group.clientList.first { it.status == WifiP2pDevice.CONNECTED }
                        } else {
                            group.owner
                        }?.toWifiDevice(group.networkName)
                    }
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
//                val device = intent.getExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
//                device?.let {
//                    Log.d(
//                        "WifiConnectionManager",
//                        "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ${it.toWifiDevice()}"
//                    )
//                }
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
        disconnect()
    }

    override fun disconnect() {
        if (channel != null) {
            wifiManager.requestGroupInfo(channel) { group ->
                if (group != null) {
                    wifiManager.removeGroup(channel, object : ActionListener {
                        override fun onSuccess() {
                            wasConnected = false
                            _wifiState.update { WifiState.Disconnected }
                        }

                        override fun onFailure(reason: Int) {}
                    })
                }
            }
        }
    }

    override fun connect(device: WifiDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.address
            wps.setup = WpsInfo.PBC
        }

        wifiManager.connect(channel, config, object : ActionListener {
            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
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

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        devices = peerList.deviceList.map { it.toWifiDevice() }
        _wifiState.update { WifiState.DevicesFound(devices) }
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        val groupOwnerAddress = info.groupOwnerAddress
        val device = connectedDevice ?: WifiDevice("Unknown", "", "Unknown Network")
        if (info.groupFormed && info.isGroupOwner) { //Server
            communication.configureAsServer(device)
        } else if (info.groupFormed) { // Client
            communication.configureAsClient(device, groupOwnerAddress)
        }
        _wifiState.update { WifiState.Connected(device) }
    }
}

private fun WifiP2pDevice.toWifiDevice(groupName: String? = null): WifiDevice =
    WifiDevice(deviceName, deviceAddress, groupName ?: "")

