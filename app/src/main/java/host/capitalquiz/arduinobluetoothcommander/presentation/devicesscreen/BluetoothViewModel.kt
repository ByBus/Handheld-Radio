package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ConnectionResultUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val repository: DevicesRepository,
    private val deviceUiMapper: DeviceMapper<DeviceUi>,
    private val connectionResultUiMapper: ConnectionResult.Mapper<ConnectionResultUi>,
    private val communication: Communication,
) : ViewModel() {

    private val _message = MutableStateFlow<Event>(Event.Empty)
    val message = _message.asStateFlow()
    private val oldState = MutableStateFlow(BluetoothUiState())

    val state = combine(
        oldState,
        repository.pairedDevices,
        repository.scannedDevices,
        communication.connectionState
    ) { old, paired, scanned, connect ->
        (old.copy(
            pairedDevices = paired.map { it.map(deviceUiMapper) },
            scannedDevices = scanned.map { it.map(deviceUiMapper) }
        ) + connect.map { it.map(connectionResultUiMapper) })
            .also { it.message(::send) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), oldState.value)


    fun startScanning() {
        oldState.update {
            it.copy(isDiscoveringDevices = true, toastMessage = null)
        }
        repository.discoverDevices()
    }

    fun stopScanning() {
        oldState.update {
            it.copy(isDiscoveringDevices = false, toastMessage = null)
        }
        repository.stopDiscoveringDevices()
    }

    fun connectTo(deviceUi: DeviceUi) {
        viewModelScope.launch {
            val device = deviceUi.toDomain()
            communication.connectToDevice(device)
        }
    }

    fun startServer(serverName: String) {
        viewModelScope.launch {
            communication.startServer(serverName)
        }
    }

    private fun send(message: String) {
        _message.tryEmit(Event.Text(message))
    }

    override fun onCleared() {
        stopScanning()
        communication.close()
        super.onCleared()
    }
}