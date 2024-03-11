package host.capitalquiz.arduinobluetoothcommander.domain

sealed interface ConnectionResult {
    fun <R> map(mapper: Mapper<R>): R = mapper(this)
    fun abortErrorOrSelf() = this

    object Idle : ConnectionResult
    object Connected : ConnectionResult
    class Connecting(val duration: Int) : ConnectionResult {
        override fun abortErrorOrSelf(): ConnectionResult = ConnectionError.AbortConnection
    }

    data class Connect(val device: Device) : ConnectionResult
    data class Disconnect(val device: Device) : ConnectionResult

    fun interface Mapper<R> {
        operator fun invoke(result: ConnectionResult): R
    }
}

sealed interface ConnectionError : ConnectionResult {
    data class Error(val message: String) : ConnectionError
    object Timeout : ConnectionError
    object SocketBusy : ConnectionError
    object AbortConnection : ConnectionResult
}