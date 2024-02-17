package host.capitalquiz.arduinobluetoothcommander.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothChecker
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val repository: DevicesRepository,
    private val bluetoothStatus: BluetoothChecker,
    private val uiStateMapper: DeviceMapper<DeviceUi>,
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        repository.pairedDevices,
        repository.scannedDevices,
        _state
    ) { paired, scanned, oldState ->
        BluetoothUiState(
            paired.map { it.map(uiStateMapper) },
            scanned.map { it.map(uiStateMapper) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    fun startScanning() = repository.discoverDevices()

    fun stopScanning() = repository.stopDiscoveringDevices()

    fun isBlueToothEnabled(): Boolean = bluetoothStatus.isEnabled()
}