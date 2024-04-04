package host.capitalquiz.bluetoothchat.domain.chat

interface BluetoothMessageMapper<R> {
    operator fun invoke(message: String, name: String, fromMe: Boolean, time: Long): R
}