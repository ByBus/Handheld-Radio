package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("MissingPermission")
class FoundDevicesReceiver@Inject constructor(@ApplicationContext private val context: Context) : BroadcastReceiver(),
    DevicesClosableDataSource {
    private val _foundDevices = MutableStateFlow<List<Device>>(emptyList())
    override val foundDevices = _foundDevices.asStateFlow()

    override fun onReceive(context: Context, intent: Intent) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME, BluetoothDevice::class.java)
        } else {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME)
        }
        device?.let {
            _foundDevices.update { old ->
                val new = Device(it.name, it.address)
                if (new !in old) old else old + new
            }
        }
    }

    override fun init() {
        context.registerReceiver(this, IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    override fun close() {
        context.unregisterReceiver(this)
    }
}