package host.capitalquiz.arduinobluetoothcommander.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val repository: DevicesRepository,
    private val deviceUiMapper: DeviceMapper<DeviceUi>,
    private val connectionResultUiMapper: ConnectionResult.Mapper<ConnectionResultUi>,
    private val communication: Communication,
) : ViewModel() {

    private val _message = MutableStateFlow<MessageEvent>(MessageEvent.Empty)
    val message = _message.asStateFlow()

    val state = combine(
        repository.pairedDevices,
        repository.scannedDevices,
        communication.connectionState
    ) { paired, scanned, connect ->
        (BluetoothUiState(
            paired.map { it.map(deviceUiMapper) },
            scanned.map { it.map(deviceUiMapper) }
        ) + connect.map { it.map(connectionResultUiMapper) })
            .apply { errorMessage(::send) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BluetoothUiState())


    fun startScanning() = repository.discoverDevices()

    fun stopScanning() = repository.stopDiscoveringDevices()

    fun connectTo(deviceUi: DeviceUi) {
        viewModelScope.launch {
            val device = deviceUi.toDomain()
            communication.connectToDevice(device)
        }
    }

    fun disconnect() {
        communication.close()
    }

    fun startServer(serverName: String) {
        viewModelScope.launch {
            communication.startServer(serverName)
        }
    }

    private fun send(message: String) {
        _message.tryEmit(MessageEvent.Text(message))
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
        communication.close()
    }
}