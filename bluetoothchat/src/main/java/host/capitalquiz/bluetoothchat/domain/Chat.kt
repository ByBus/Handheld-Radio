package host.capitalquiz.bluetoothchat.domain

data class Chat(
    val id: Long,
    val name: String,
    val mac: String,
    val date: Long,
    val messages: List<Message> = emptyList(),
)
