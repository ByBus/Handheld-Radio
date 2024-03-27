package host.capitalquiz.wifiradioset.presentation.devices

interface BaseEvent {
    fun navigate(consumer: () -> Unit): Event
    fun message(consumer: (String) -> Unit): Event
}

interface StreamEvent : BaseEvent {
    fun audioSessionReady(consumer: (Int) -> Unit): Event
    fun connectionReady(consumer: () -> Unit): Event
}

interface Event : StreamEvent {

    abstract class BaseEvent : Event {
        override fun navigate(consumer: () -> Unit): Event = this
        override fun message(consumer: (String) -> Unit): Event = this
        override fun audioSessionReady(consumer: (Int) -> Unit): Event = this
        override fun connectionReady(consumer: () -> Unit): Event = this
    }

    object Empty : BaseEvent()

    open class Toast(private val text: String) : BaseEvent() {
        override fun message(consumer: (String) -> Unit): Event {
            consumer(text)
            return this
        }
    }

    object Navigation : BaseEvent() {
        override fun navigate(consumer: () -> Unit): Event {
            consumer()
            return this
        }
    }

    class ToastWithNavigation(message: String) : Toast(message) {
        override fun navigate(consumer: () -> Unit): Event {
            consumer()
            return this
        }
    }

    class AudioSessionReady(private val sessionId: Int) : BaseEvent() {
        override fun audioSessionReady(consumer: (Int) -> Unit): Event {
            consumer(sessionId)
            return this
        }
    }

    class ConnectionReady(message: String) : Toast(message) {
        override fun connectionReady(consumer: () -> Unit): Event {
            consumer()
            return this
        }
    }
}
