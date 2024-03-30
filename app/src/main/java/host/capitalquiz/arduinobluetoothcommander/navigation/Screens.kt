package host.capitalquiz.arduinobluetoothcommander.navigation

interface Screens {
    val route: String

    abstract class BaseScreen(
        protected val baseRoute: String,
        private vararg val arguments: String,
    ) : Screens {
        protected fun argumentN(n: Int): String = arguments[n]
        protected fun destination(vararg values: String): String =
            "$baseRoute?${formatArgs(arguments, values)}"

        override val route = if (arguments.isEmpty()) baseRoute else "$baseRoute?${
            formatArgs(
                arguments,
                arguments,
                true
            )
        }"

        private fun formatArgs(
            names: Array<out String>,
            values: Array<out String>,
            route: Boolean = false,
        ): String = with(StringBuilder()) {
            names.zip(values) { name, value ->
                append(name).append("=")
                if (route) append("{")
                append(value)
                if (route) append("}")
                append("&")
            }
            setLength(length - 1)
            this.toString()
        }
    }

    object ChatDevices : BaseScreen(baseRoute = "devices")

    object Chat : BaseScreen(baseRoute = "bluetoothChat", "chatName", "macAddress") {
        val MAC = argumentN(0)
        val CHAT_NAME = argumentN(1)
        fun route(chatName: String, macAddress: String): String = destination(chatName, macAddress)
    }

    object RadioSetDevices : BaseScreen(baseRoute = "radioSet") {
        const val DISCONNECT = "disconnect"
    }

    object AudioConversation :
        BaseScreen(baseRoute = "conversation", "deviceName", "deviceMac", "deviceNetwork") {
        val DEVICE_NAME = argumentN(0)
        val DEVICE_MAC = argumentN(1)
        val NETWORK = argumentN(2)
        fun route(deviceName: String, deviceMac: String, network: String): String =
            destination(deviceName, deviceMac, network)
    }
}
