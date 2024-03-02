package host.capitalquiz.arduinobluetoothcommander.domain

interface BluetoothMessageMapper<R> {
    operator fun invoke(message: String, name: String, fromMe: Boolean, time: Long): R
}