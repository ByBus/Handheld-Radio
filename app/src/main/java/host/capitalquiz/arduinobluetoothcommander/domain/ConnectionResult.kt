package host.capitalquiz.arduinobluetoothcommander.domain

sealed interface ConnectionResult {
    fun <R> map(mapper: Mapper<R>): R = mapper(this)

    object Idle : ConnectionResult
    object Connected : ConnectionResult
    class Connecting : ConnectionResult
    data class Error(val message: String) : ConnectionResult
    data class Connect(val device: Device) : ConnectionResult
    data class Disconnect(val device: Device) : ConnectionResult

    fun interface Mapper<R> {
        operator fun invoke(connectionResult: ConnectionResult): R
    }
}