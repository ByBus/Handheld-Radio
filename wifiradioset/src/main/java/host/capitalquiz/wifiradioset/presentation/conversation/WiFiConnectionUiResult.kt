package host.capitalquiz.wifiradioset.presentation.conversation

import host.capitalquiz.wifiradioset.presentation.devices.WiFiEvent

sealed interface WiFiConnectionUiResult {

    fun produceEvent(): WiFiEvent

    abstract class BaseResult : WiFiConnectionUiResult {
        protected abstract val message: String
        override fun produceEvent(): WiFiEvent = WiFiEvent.Toast(message)
    }

    abstract class ResultWithNavigation : BaseResult() {
        override fun produceEvent(): WiFiEvent = WiFiEvent.ToastWithNavigation(message)
    }

    object Idle : WiFiConnectionUiResult {
        override fun produceEvent(): WiFiEvent = WiFiEvent.Empty
    }

    class Streaming(private val audioSessionId: Int) : WiFiConnectionUiResult {
        override fun produceEvent(): WiFiEvent = WiFiEvent.AudioSessionReadyEvent(audioSessionId)
    }

    data class Error(override val message: String) : ResultWithNavigation()
    class Connect(override val message: String) : BaseResult()
    class Disconnect(override val message: String) : ResultWithNavigation()
    data class Abort(override val message: String) : ResultWithNavigation()
}