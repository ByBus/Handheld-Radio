package host.capitalquiz.arduinobluetoothcommander.data

import android.bluetooth.BluetoothManager
import android.util.Log
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothChecker
import javax.inject.Inject

class BluetoothStatus @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
) : BluetoothChecker {
    override fun isEnabled(): Boolean {
        Log.d("BluetoothStatus", "isEnabled: $bluetoothManager")
        return bluetoothManager?.adapter?.isEnabled == true
    }
}