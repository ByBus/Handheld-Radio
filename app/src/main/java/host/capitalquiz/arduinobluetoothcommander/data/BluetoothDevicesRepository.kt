package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.di.PairedDevices
import host.capitalquiz.arduinobluetoothcommander.di.ScannedDevices
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import javax.inject.Inject

class BluetoothDevicesRepository @Inject constructor(
    @PairedDevices private val pairedDevicesDataSource: DevicesClosableDataSource,
    @ScannedDevices private val scannedDevicesDataSource: DevicesClosableDataSource
): DevicesRepository {

    override val pairedDevices = pairedDevicesDataSource.foundDevices
    override val scannedDevices = scannedDevicesDataSource.foundDevices


    override fun discoverDevices() {
        pairedDevicesDataSource.init()
        scannedDevicesDataSource.init()
    }

    override fun stopDiscoveringDevices() {
        pairedDevicesDataSource.close()
        scannedDevicesDataSource.close()
    }
}