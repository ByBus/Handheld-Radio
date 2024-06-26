package host.capitalquiz.bluetoothchat.data.communication

import android.bluetooth.BluetoothManager
import host.capitalquiz.common.di.DispatcherIO
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
        override fun createServer(): Server = Server.BluetoothServer(bluetoothManager, dispatcher)

        override fun createClient(): Client =
            Client.BluetoothClient(bluetoothManager, connectionWatcher, dispatcher)

    }
}