package host.capitalquiz.arduinobluetoothcommander.domain

data class Message(
    val id: Long,
    val name: String,
    val text: String,
    val date: Long,
    val chatId: Long,
    val fromMe: Boolean = false,
)