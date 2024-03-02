package host.capitalquiz.arduinobluetoothcommander.data

import android.bluetooth.BluetoothSocket

interface SocketHolder : Closable {
    var socket: BluetoothSocket?

    object EMPTY : SocketHolder {
        override var socket: BluetoothSocket? = null

        override fun init() = Unit

        override fun close() = Unit
    }
}
