package host.capitalquiz.bluetoothchat.data.devices

import android.bluetooth.BluetoothManager
import host.capitalquiz.bluetoothchat.domain.BluetoothChecker
import javax.inject.Inject

class BluetoothStatus @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
) : BluetoothChecker {
    override fun isEnabled(): Boolean {
        return bluetoothManager?.adapter?.isEnabled == true
    }
}