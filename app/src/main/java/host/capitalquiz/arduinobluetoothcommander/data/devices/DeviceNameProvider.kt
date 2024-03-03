package host.capitalquiz.arduinobluetoothcommander.data.devices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import javax.inject.Inject

interface DeviceNameProvider {
    fun provide(): String

    @SuppressLint("MissingPermission")
    class BluetoothName @Inject constructor(
        private val bluetoothManager: BluetoothManager?,
    ) : DeviceNameProvider {
        private val name by lazy { bluetoothManager?.adapter?.name ?: "" }

        override fun provide(): String = name
    }
}