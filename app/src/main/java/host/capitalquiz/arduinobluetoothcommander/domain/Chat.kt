package host.capitalquiz.arduinobluetoothcommander.domain

data class Chat(
    val id: Long,
    val name: String,
    val date: Long,
    val messages: List<Message> = emptyList(),
)
