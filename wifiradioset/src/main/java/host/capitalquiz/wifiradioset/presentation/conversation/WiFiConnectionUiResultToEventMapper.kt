package host.capitalquiz.wifiradioset.presentation.conversation

import host.capitalquiz.common.presentation.ResourceProvider
import host.capitalquiz.wifiradioset.R

import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.presentation.devices.Event
import javax.inject.Inject

class WiFiConnectionUiResultToEventMapper @Inject constructor(
    private val stringProvider: ResourceProvider<String>,
) : WiFiConnectionResult.Mapper<Event> {
    override fun invoke(result: WiFiConnectionResult): Event {
        return when (result) {
            WiFiConnectionResult.Abort -> Event.ToastWithNavigation(String(R.string.connection_was_aborted))
            is WiFiConnectionResult.Connect -> Event.ConnectionReady(
                "${result.device.name} ${String(R.string.client_was_connected)}"
            )

            is WiFiConnectionResult.Disconnect -> Event.ToastWithNavigation(
                "${result.device.name} ${String(R.string.client_was_disconnected)}"
            )

            is WiFiConnectionResult.Error -> Event.ToastWithNavigation(result.message)
            WiFiConnectionResult.Idle -> Event.Empty
            is WiFiConnectionResult.Streaming -> Event.AudioSessionReady(result.audioSessionId)
        }
    }

    private fun String(id: Int): String = stringProvider.provide(id)
}