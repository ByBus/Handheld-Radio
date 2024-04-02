package host.capitalquiz.wifiradioset.presentation.devices.contracts

import android.Manifest
import android.os.Build
import host.capitalquiz.common.presentation.contracts.MultiplePermissionsContract

class RequestWifiPermissions : MultiplePermissionsContract() {
    override val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
    ) + (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    else emptyArray())
}