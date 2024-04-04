package host.capitalquiz.bluetoothchat.domain.chat

import java.nio.ByteBuffer

data class BluetoothMessage(
    val message: String,
    val name: String,
    val fromMe: Boolean,
    val time: Long = System.currentTimeMillis(),
) {

    fun <R> map(mapper: BluetoothMessageMapper<R>): R {
        return mapper(message, name, fromMe, time)
    }

    fun fromByteArray(bytes: ByteArray, fromMe: Boolean): BluetoothMessage {
        val buffer = ByteBuffer.wrap(bytes)
        val messageBytes = ByteArray(buffer.getInt())
        buffer.get(messageBytes)
        val nameBytes = ByteArray(buffer.getInt())
        buffer.get(nameBytes)
        val time = buffer.getLong()
        return BluetoothMessage(
            messageBytes.decodeToString(),
            nameBytes.decodeToString(),
            fromMe,
            time
        )
    }
}
