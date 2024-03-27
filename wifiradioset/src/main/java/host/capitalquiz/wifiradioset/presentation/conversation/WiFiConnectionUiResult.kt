package host.capitalquiz.wifiradioset.presentation.conversation

import host.capitalquiz.wifiradioset.presentation.devices.Event

sealed interface WiFiConnectionUiResult {

    fun produceEvent(): Event

    abstract class BaseResult : WiFiConnectionUiResult {
        protected abstract val message: String
        override fun produceEvent(): Event = Event.Toast(message)
    }

    abstract class ResultWithNavigation : BaseResult() {
        override fun produceEvent(): Event = Event.ToastWithNavigation(message)
    }

    object Idle : WiFiConnectionUiResult {
        override fun produceEvent(): Event = Event.Empty
    }

    class Streaming(private val audioSessionId: Int) : WiFiConnectionUiResult {
        override fun produceEvent(): Event = Event.AudioSessionReadyWiFiEventBase(audioSessionId)
    }

    data class Error(override val message: String) : ResultWithNavigation()
    class Connect(override val message: String) : BaseResult()
    class Disconnect(override val message: String) : ResultWithNavigation()
    data class Abort(override val message: String) : ResultWithNavigation()
}