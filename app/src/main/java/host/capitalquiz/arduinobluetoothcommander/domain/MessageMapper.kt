package host.capitalquiz.arduinobluetoothcommander.domain

fun interface MessageMapper<R> {
    operator fun invoke(
        id: Long,
        name: String,
        text: String,
        date: Long,
        chatId: Long,
        fromMe: Boolean,
    ): R
}
