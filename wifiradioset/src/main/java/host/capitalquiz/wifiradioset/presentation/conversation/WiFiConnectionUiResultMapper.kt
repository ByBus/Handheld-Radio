package host.capitalquiz.wifiradioset.presentation.conversation

import host.capitalquiz.common.presentation.ResourceProvider
import host.capitalquiz.wifiradioset.R

import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import javax.inject.Inject

class WiFiConnectionUiResultMapper @Inject constructor(
    private val stringProvider: ResourceProvider<String>,
) : WiFiConnectionResult.Mapper<WiFiConnectionUiResult> {
    override fun invoke(result: WiFiConnectionResult): WiFiConnectionUiResult {
        return when (result) {
            WiFiConnectionResult.Abort -> WiFiConnectionUiResult.Abort(String(R.string.connection_was_aborted))
            is WiFiConnectionResult.Connect -> WiFiConnectionUiResult.Connect(
                "${result.device.name} ${String(R.string.client_was_connected)}"
            )

            is WiFiConnectionResult.Disconnect -> WiFiConnectionUiResult.Connect(
                "${result.device.name} ${String(R.string.client_was_disconnected)}"
            )

            is WiFiConnectionResult.Error -> WiFiConnectionUiResult.Error(result.message)
            WiFiConnectionResult.Idle -> WiFiConnectionUiResult.Idle
            is WiFiConnectionResult.Streaming -> WiFiConnectionUiResult.Streaming(result.audioSessionId)
        }
    }

    private fun String(id: Int): String = stringProvider.provide(id)
}