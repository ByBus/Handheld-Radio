package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothDeviceStateReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
) : BroadcastReceiver(), DeviceConnectionWatcher {
    private var isRegistered = false
    private var currentWatchingDevice = Device("", "")
    private val _connectionState = MutableStateFlow<ConnectionResult>(ConnectionResult.Idle)
    override val connectionState = _connectionState.asStateFlow()

    override fun onReceive(context: Context, intent: Intent) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
        if (currentWatchingDevice.mac != device?.address) return

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> ConnectionResult.Connect(currentWatchingDevice)
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> ConnectionResult.Disconnect(
                currentWatchingDevice
            )

            else -> return
        }.let { event ->
            _connectionState.tryEmit(event)
        }
    }

    override fun watchFor(device: Device) {
        currentWatchingDevice = device
    }

    override fun init() {
        if (isRegistered.not()) {
            context.registerReceiver(
                this,
                IntentFilter().apply {
                    addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                    addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                    addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                }
            )
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
