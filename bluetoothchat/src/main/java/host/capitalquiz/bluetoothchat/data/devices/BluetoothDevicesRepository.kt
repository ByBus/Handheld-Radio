package host.capitalquiz.bluetoothchat.data.devices

import host.capitalquiz.bluetoothchat.di.PairedDevices
import host.capitalquiz.bluetoothchat.di.ScannedDevices
import host.capitalquiz.bluetoothchat.domain.devices.DevicesRepository
import javax.inject.Inject

class BluetoothDevicesRepository @Inject constructor(
    private val myNameProvider: DeviceNameProvider,
    @PairedDevices private val pairedDevicesDataSource: DevicesClosableDataSource,
    @ScannedDevices private val scannedDevicesDataSource: DevicesClosableDataSource,
): DevicesRepository {

    override val pairedDevices = pairedDevicesDataSource.foundDevices
    override val scannedDevices = scannedDevicesDataSource.foundDevices

    override val myDeviceName get() = myNameProvider.provide()

    override fun discoverDevices() {
        pairedDevicesDataSource.init()
        scannedDevicesDataSource.init()
    }

    override fun stopDiscoveringDevices() {
        pairedDevicesDataSource.close()
        scannedDevicesDataSource.close()
    }
}