package host.capitalquiz.bluetoothchat.domain

data class Message(
    val id: Long = -1,
    val name: String = "",
    val text: String,
    val date: Long = System.currentTimeMillis(),
    val chatId: Long = -1,
    val fromMe: Boolean = false,
) {
    fun <R> map(mapper: MessageMapper<R>): R {
        return mapper(id, name, text, date, chatId, fromMe)
    }
}

fun <R> List<Message>.mapItems(mapper: MessageMapper<R>): List<R> = this.map { it.map(mapper) }