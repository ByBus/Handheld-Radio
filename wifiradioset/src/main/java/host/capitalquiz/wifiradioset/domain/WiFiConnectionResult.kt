package host.capitalquiz.wifiradioset.domain

sealed interface WiFiConnectionResult {
    val isSuccessConnection: Boolean

    sealed class BaseConnectionResult : WiFiConnectionResult {
        override val isSuccessConnection = false
    }

    object Idle : BaseConnectionResult()
    data class Error(val message: String) : BaseConnectionResult()
    class Connect(val device: WifiDevice) : WiFiConnectionResult {
        override val isSuccessConnection = true
    }

    class Disconnect(val device: WifiDevice) : BaseConnectionResult()
    object Abort : BaseConnectionResult()

    fun <R> map(mapper: Mapper<R>): R = mapper(this)

    fun interface Mapper<R> {
        operator fun invoke(result: WiFiConnectionResult): R
    }
}