package host.capitalquiz.wifiradioset.presentation.devices

interface WiFiEvent {
    fun navigate(consumer: () -> Unit): WiFiEvent
    fun message(consumer: (String) -> Unit): WiFiEvent

    abstract class BaseEvent : WiFiEvent {
        override fun navigate(consumer: () -> Unit): WiFiEvent = this
        override fun message(consumer: (String) -> Unit): WiFiEvent = this
    }

    object Empty : BaseEvent()

    class Toast(private val text: String) : BaseEvent() {
        override fun message(consumer: (String) -> Unit): WiFiEvent {
            consumer(text)
            return this
        }
    }

    object Navigation : BaseEvent() {
        override fun navigate(consumer: () -> Unit): WiFiEvent {
            consumer()
            return this
        }
    }

    class ToastWithNavigation(private val text: String) : WiFiEvent {
        override fun message(consumer: (String) -> Unit): WiFiEvent {
            consumer(text)
            return this
        }

        override fun navigate(consumer: () -> Unit): WiFiEvent {
            consumer()
            return this
        }
    }
}