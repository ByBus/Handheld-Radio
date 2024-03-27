package host.capitalquiz.bluetoothchat.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.Device
import host.capitalquiz.common.getExtra
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothDeviceStateReceiver @Inject constructor(
    @ApplicationContext private val context: Context,
) : BroadcastReceiver(), DeviceConnectionWatcher {
    private var isRegistered = false
    private var currentWatchingDevice = Device("", "")
    private var listener: ((ConnectionResult) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val device = intent.getExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (currentWatchingDevice.mac != device?.address) return

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> ConnectionResult.Connect(currentWatchingDevice)
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> ConnectionResult.Disconnect(
                currentWatchingDevice
            )
            else -> return
        }.let { event ->
            listener?.invoke(event)
        }
    }

    override fun listenForConnectionResult(callback: ((ConnectionResult) -> Unit)?) {
        this.listener = callback
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
        listener = null
    }
}
