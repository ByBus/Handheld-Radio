package host.capitalquiz.bluetoothchat.presentation.devicesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.bluetoothchat.domain.Communication
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.DeviceMapper
import host.capitalquiz.bluetoothchat.domain.DevicesRepository
import host.capitalquiz.bluetoothchat.domain.SingletonFactory
import host.capitalquiz.bluetoothchat.domain.mapItems
import host.capitalquiz.bluetoothchat.presentation.ConnectionResultUi
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
    private val communication: SingletonFactory<Communication>,
) : ViewModel() {

    private val _message = MutableStateFlow<Event>(Event.Empty)
    val message = _message.asStateFlow()
    private val oldState = MutableStateFlow(BluetoothUiState())

    val state = combine(
        oldState,
        repository.pairedDevices,
        repository.scannedDevices,
    ) { old, pairedDevices, scannedDevices ->
        old.copy(
            pairedDevices = pairedDevices.mapItems(deviceUiMapper),
            scannedDevices = scannedDevices.mapItems(deviceUiMapper)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), oldState.value)

    init {
        viewModelScope.launch {
            communication.create().connectionState.collect { connectionResult ->
                oldState.update {
                    connectionResult
                        .map(connectionResultUiMapper)
                        .reduce(it, ::send)
                }
            }
        }
    }

    fun disconnect() = communication.provide().close()

    fun startScanning() {
        oldState.update { it.copy(isDiscoveringDevices = true) }
        repository.discoverDevices()
    }

    fun stopScanning() {
        oldState.update { it.copy(isDiscoveringDevices = false) }
        repository.stopDiscoveringDevices()
    }

    fun connectTo(deviceUi: DeviceUi) {
        viewModelScope.launch {
            val device = deviceUi.toDomain()
            communication.provide().connectToDevice(device)
        }
    }

    fun startServer(serverName: String) {
        viewModelScope.launch { communication.provide().startServer(serverName) }
    }

    private fun send(message: String) {
        _message.tryEmit(Event.Text(message))
    }

    override fun onCleared() {
        stopScanning()
        communication.provide().close()
        communication.recycle()
        super.onCleared()
    }
}