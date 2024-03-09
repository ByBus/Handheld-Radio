package host.capitalquiz.arduinobluetoothcommander.presentation.navigation

enum class Screens() {
    Devices {
        override val arg1Name = ""
        override val route = "devices"
        override fun destination(arg1: String): String = ""
    },
    Chat {
        override val arg1Name: String = "chatName"
        override val route = "bluetoothChat?$arg1Name={chatName}"
        override fun destination(arg1: String): String = "bluetoothChat?$arg1Name=$arg1"
    };

    abstract val arg1Name: String
    abstract val route: String
    abstract fun destination(arg1: String): String
}
