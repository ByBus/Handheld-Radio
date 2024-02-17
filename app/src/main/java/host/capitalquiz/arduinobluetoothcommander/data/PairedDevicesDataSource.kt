package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@SuppressLint("MissingPermission")
class PairedDevicesDataSource @Inject constructor(
    private val bluetoothManager: BluetoothManager?
) : DevicesClosableDataSource {
    private val _pairedDevices = MutableStateFlow<List<Device>>(emptyList())
    override val foundDevices = _pairedDevices.asStateFlow()

    private val adapter by lazy { bluetoothManager?.adapter }

    override fun init() {
        refreshPaired()
        bluetoothManager?.adapter?.startDiscovery()
    }

    override fun close() {
        bluetoothManager?.adapter?.cancelDiscovery()
    }

    private fun refreshPaired() {
        adapter?.bondedDevices?.map { device ->
            Device(device.name, device.address)
        }?.let { devices ->
            _pairedDevices.update { devices }
        }
    }
}