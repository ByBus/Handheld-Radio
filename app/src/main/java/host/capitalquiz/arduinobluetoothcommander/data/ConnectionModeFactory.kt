package host.capitalquiz.arduinobluetoothcommander.data

import android.bluetooth.BluetoothManager
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface ConnectionModeFactory {
    fun createServer(): Server
    fun createClient(): Client

    class Base @Inject constructor(
        private val bluetoothManager: BluetoothManager?,
        private val connectionWatcher: DeviceConnectionWatcher,
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : ConnectionModeFactory {
        override fun createServer(): Server = BluetoothServer(bluetoothManager, dispatcher)

        override fun createClient(): Client =
            BluetoothClient(bluetoothManager, connectionWatcher, dispatcher)

    }
}