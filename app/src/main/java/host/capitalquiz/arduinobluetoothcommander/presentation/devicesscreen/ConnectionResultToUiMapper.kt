package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.presentation.ConnectionResultUi
import host.capitalquiz.arduinobluetoothcommander.presentation.ResourceProvider
import javax.inject.Inject
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionError as Error
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult as Result

class ConnectionResultToUiMapper @Inject constructor(
    private val stringProvider: ResourceProvider<String>,
) : Result.Mapper<ConnectionResultUi> {
    override fun invoke(result: Result): ConnectionResultUi {
        return when (result) {
            is Result.Idle -> ConnectionResultUi.Idle
            is Result.Connected -> ConnectionResultUi.ConnectionEstablished
            is Result.Connect ->
                ConnectionResultUi.DeviceConnected(
                    result.device,
                    String(R.string.connected)
                )

            is Result.Disconnect ->
                ConnectionResultUi.DeviceDisconnected(
                    result.device,
                    String(R.string.was_disconnected)
                )

            is Result.Connecting -> ConnectionResultUi.Connecting(result.duration)
            is Error.Timeout -> ConnectionResultUi.Error(String(R.string.timeout_exceeded))
            is Error.AbortConnection -> ConnectionResultUi.Error(String(R.string.connection_aborted))
            is Error.SocketBusy -> ConnectionResultUi.Error(String(R.string.please_try_again))
            is Error.Error -> ConnectionResultUi.Error(result.message)
        }
    }

    private fun String(id: Int): String = stringProvider.provide(id)
}