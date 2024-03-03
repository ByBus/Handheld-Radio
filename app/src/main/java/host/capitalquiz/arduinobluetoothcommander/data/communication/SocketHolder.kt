package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.data.Closable

interface SocketHolder : Closable {
    var socket: BluetoothSocket?

    object EMPTY : SocketHolder {
        override var socket: BluetoothSocket? = null

        override fun init() = Unit

        override fun close() = Unit
    }
}
