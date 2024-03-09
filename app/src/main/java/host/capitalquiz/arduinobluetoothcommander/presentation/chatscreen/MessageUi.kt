package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

data class MessageUi(
    val id: Long,
    val name: String,
    val text: String,
    val date: String,
    val chatId: Long,
    val fromMe: Boolean = false,
)
