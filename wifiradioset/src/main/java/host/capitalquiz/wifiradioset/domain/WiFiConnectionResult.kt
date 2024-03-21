package host.capitalquiz.wifiradioset.domain

sealed interface WiFiConnectionResult {
    object Idle : WiFiConnectionResult
    data class Error(val message: String) : WiFiConnectionResult
    class Connect(val device: WifiDevice) : WiFiConnectionResult
    class Disconnect(val device: WifiDevice) : WiFiConnectionResult
    object Abort : WiFiConnectionResult

    fun <R> map(mapper: WiFiConnectionResult.Mapper<R>): R = mapper(this)

    fun interface Mapper<R> {
        operator fun invoke(result: WiFiConnectionResult): R
    }
}