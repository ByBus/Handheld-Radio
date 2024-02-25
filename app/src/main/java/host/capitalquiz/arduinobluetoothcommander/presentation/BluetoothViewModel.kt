package host.capitalquiz.arduinobluetoothcommander.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothChecker
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.toDomain
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val repository: DevicesRepository,
    private val bluetoothStatus: BluetoothChecker,
    private val deviceUiMapper: DeviceMapper<DeviceUi>,
    private val connectionResultUiMapper: ConnectionResult.Mapper<ConnectionResultUi>,
    private val communication: Communication,
) : ViewModel() {

    private val connectionState =
        communication.connectionState.map { it.map(connectionResultUiMapper) }
    val state = combine(
        repository.pairedDevices,
        repository.scannedDevices,
        connectionState
    ) { paired, scanned, connect ->
        BluetoothUiState(
            paired.map { it.map(deviceUiMapper) },
            scanned.map { it.map(deviceUiMapper) }
        ) + connect
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BluetoothUiState())


    fun startScanning() = repository.discoverDevices()

    fun stopScanning() = repository.stopDiscoveringDevices()

    fun isBlueToothEnabled(): Boolean = bluetoothStatus.isEnabled()


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

    override fun onCleared() {
        super.onCleared()
        stopScanning()
        communication.close()
    }
}