package host.capitalquiz.arduinobluetoothcommander.presentation.navigation

enum class Screens(vararg val arguments: String) {
    Devices {
        override val route = "devices"
        override fun destination(vararg values: String): String = ""
    },
    Chat("chatName", "macAddress") {
        override val route = "bluetoothChat?${formatArgs(arguments, arguments, true)}"
        override fun destination(vararg values: String): String =
            "bluetoothChat?${formatArgs(arguments, values)}"
    };

    fun argumentN(n: Int): String = arguments[n]
    abstract val route: String
    abstract fun destination(vararg values: String): String

    protected fun formatArgs(
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
