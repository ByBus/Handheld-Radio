package host.capitalquiz.wifiradioset.presentation.devices

import host.capitalquiz.wifiradioset.domain.WifiDevice

interface SimpleEvent {
    fun navigate(consumer: (deviceName: String, mac: String, network: String) -> Unit): Event
    fun message(consumer: (String) -> Unit): Event
}

interface StreamEvent : SimpleEvent {
    fun audioSessionReady(consumer: (Int) -> Unit): Event
    fun connectionReady(consumer: () -> Unit): Event
    fun navigate(consumer: () -> Unit): Event
}

interface Event : StreamEvent {

    abstract class BaseEvent : Event {
        override fun navigate(
            consumer: (deviceName: String, mac: String, network: String) -> Unit,
        ): Event = this
        override fun navigate(consumer: () -> Unit): Event = this
        override fun message(consumer: (String) -> Unit): Event = this
        override fun audioSessionReady(consumer: (Int) -> Unit): Event = this
        override fun connectionReady(consumer: () -> Unit): Event = this
    }

    object Empty : BaseEvent()

    open class Toast(private val text: String) : BaseEvent() {
        override fun message(consumer: (String) -> Unit): Event = this.also { consumer(text) }
    }

    object Navigation : BaseEvent() {
        override fun navigate(consumer: () -> Unit): Event = this.also { consumer() }
    }

    class ToastWithNavigation(message: String) : Toast(message) {
        override fun navigate(consumer: () -> Unit): Event = this.also { consumer() }
    }

    class ToDeviceNavigation(private val device: WifiDevice) : BaseEvent() {
        override fun navigate(consumer: (deviceName: String, mac: String, network: String) -> Unit): Event =
            this.also { consumer(device.name, device.address, device.groupName) }
    }

    class AudioSessionReady(private val sessionId: Int) : BaseEvent() {
        override fun audioSessionReady(consumer: (Int) -> Unit): Event =
            this.also { consumer(sessionId) }
    }

    class ConnectionReady(message: String) : Toast(message) {
        override fun connectionReady(consumer: () -> Unit): Event = this.also { consumer() }
    }
}
