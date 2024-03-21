package host.capitalquiz.wifiradioset.domain

sealed interface WifiState {
    object Idle : WifiState
    object On : WifiState
    object Off : WifiState
    data class DevicesFound(val wifiDevices: List<WifiDevice>) : WifiState
    object ConnectionFailed : WifiState
    object Connected : WifiState
    object Disconnected : WifiState

    fun <R> map(mapper: Mapper<R>): R {
        return mapper(this)
    }

    fun interface Mapper<R> {
        operator fun invoke(wifiState: WifiState): R
    }
}