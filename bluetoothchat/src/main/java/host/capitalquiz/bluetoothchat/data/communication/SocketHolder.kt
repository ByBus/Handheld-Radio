package host.capitalquiz.bluetoothchat.data.communication

import android.bluetooth.BluetoothSocket
import host.capitalquiz.common.data.Closable

interface SocketHolder : Closable {
    var socket: BluetoothSocket?

    object EMPTY : SocketHolder {
        override var socket: BluetoothSocket? = null

        override fun init() = Unit

        override fun close() = Unit
    }
}
