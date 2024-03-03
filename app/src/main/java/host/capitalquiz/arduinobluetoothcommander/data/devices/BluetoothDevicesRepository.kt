package host.capitalquiz.arduinobluetoothcommander.data.devices

import host.capitalquiz.arduinobluetoothcommander.di.PairedDevices
import host.capitalquiz.arduinobluetoothcommander.di.ScannedDevices
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
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