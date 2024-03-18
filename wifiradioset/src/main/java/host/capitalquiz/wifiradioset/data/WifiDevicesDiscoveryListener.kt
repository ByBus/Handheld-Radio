package host.capitalquiz.wifiradioset.data

import android.net.wifi.p2p.WifiP2pManager

class WifiDevicesDiscoveryListener : WifiP2pManager.ActionListener {
    override fun onSuccess() {
        // Code for when the discovery initiation is successful goes here.
        // No services have actually been discovered yet, so this method
        // can often be left blank. Code for peer discovery goes in the
        // onReceive method, detailed below.
    }

    override fun onFailure(reason: Int) {
        // Code for when the discovery initiation fails goes here.
        // Alert the user that something went wrong.
    }
}