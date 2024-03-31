package host.capitalquiz.bluetoothchat.presentation.devicesscreen.contracts

import android.Manifest
import android.os.Build
import host.capitalquiz.common.presentation.contracts.MultiplePermissionsContract

class RequestAllBluetoothPermissionsContract : MultiplePermissionsContract("", "") {
    override val permissions =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else emptyArray()
}