package host.capitalquiz.arduinobluetoothcommander.data.devices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.arduinobluetoothcommander.data.toDevice
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.common.getExtra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("MissingPermission")
class FoundDevicesReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
) : BroadcastReceiver(),
    DevicesClosableDataSource {
    private val _foundDevices = MutableStateFlow<List<Device>>(emptyList())
    override val foundDevices = _foundDevices.asStateFlow()
    private var isRegistered = false

    override fun onReceive(context: Context, intent: Intent) {
        val device = intent.getExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        device?.let { bluetoothDevice ->
            _foundDevices.update { old ->
                val new = bluetoothDevice.toDevice()
                if (new in old) old else old + new
            }
        }
    }

    override fun init() {
        if (isRegistered.not()) {
            context.registerReceiver(this, IntentFilter(BluetoothDevice.ACTION_FOUND))
            isRegistered = true
        }
    }

    override fun close() {
        if (isRegistered) {
            context.unregisterReceiver(this)
            isRegistered = false
        }
    }
}