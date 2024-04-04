package host.capitalquiz.bluetoothchat.data.communication

import host.capitalquiz.bluetoothchat.domain.chat.BluetoothMessage
import host.capitalquiz.bluetoothchat.domain.chat.BluetoothMessageMapper
import java.nio.ByteBuffer
import javax.inject.Inject

interface ByteArrayDecoder<R> {
    fun decode(bytes: ByteArray): R
}

interface BluetoothMessageDecoder : ByteArrayDecoder<BluetoothMessage>,
    BluetoothMessageMapper<ByteArray> {

    class Base @Inject constructor() : BluetoothMessageDecoder {
        override fun decode(bytes: ByteArray): BluetoothMessage {
            val buffer = ByteBuffer.wrap(bytes)
            val messageBytes = ByteArray(buffer.getInt())
            buffer.get(messageBytes)
            val nameBytes = ByteArray(buffer.getInt())
            buffer.get(nameBytes)
            val time = buffer.getLong()
            return BluetoothMessage(
                message = messageBytes.decodeToString(),
                name = nameBytes.decodeToString(),
                fromMe = false,
                time = time
            )
        }

        override fun invoke(
            message: String,
            name: String,
            fromMe: Boolean,
            time: Long,
        ): ByteArray {
            val messageBytes = message.encodeToByteArray()
            val nameBytes = name.encodeToByteArray()
            val buffer = ByteBuffer.allocate(
                Int.SIZE_BYTES + messageBytes.size +
                        Int.SIZE_BYTES + nameBytes.size +
                        Long.SIZE_BYTES
            )
            buffer
                .putInt(messageBytes.size).put(messageBytes)
                .putInt(nameBytes.size).put(nameBytes)
                .putLong(time)
            return buffer.array()
        }
    }
}