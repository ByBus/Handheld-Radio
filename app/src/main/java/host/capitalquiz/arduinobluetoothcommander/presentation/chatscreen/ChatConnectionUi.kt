package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

interface ChatConnectionUi {
    fun isConnected(): Boolean

    object Connected : ChatConnectionUi {
        override fun isConnected(): Boolean = true
    }

    data class Disconnected(val deviceName: String) : ChatConnectionUi {
        override fun isConnected(): Boolean = false
    }
}