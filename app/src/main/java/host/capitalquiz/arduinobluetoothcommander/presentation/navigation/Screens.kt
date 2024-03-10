package host.capitalquiz.arduinobluetoothcommander.presentation.navigation

enum class Screens(vararg val arguments: String) {
    Devices {
        override val route = "devices"
        override fun destination(vararg values: String): String = ""
    },
    Chat("chatName", "macAddress") {
        override val route = "bluetoothChat?${createDestination(arguments, arguments, true)}"
        override fun destination(vararg values: String): String =
            "bluetoothChat?${createDestination(arguments, values)}"
    };

    fun argumentN(n: Int): String = arguments[n]
    abstract val route: String
    abstract fun destination(vararg values: String): String

    fun createDestination(
        names: Array<out String>,
        values: Array<out String>,
        route: Boolean = false,
    ): String {
        val sb = StringBuilder()
        names.zip(values) { name, value ->
            with(sb) {
                append(name).append("=")
                if (route) append("{")
                append(value)
                if (route) append("}")
                append("&")
            }
        }
        sb.setLength(sb.length - 1)
        return sb.toString()
    }
}
