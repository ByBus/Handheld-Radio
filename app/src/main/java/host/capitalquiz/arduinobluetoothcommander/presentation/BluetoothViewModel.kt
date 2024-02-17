package host.capitalquiz.arduinobluetoothcommander.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


class BluetoothViewModel @Inject constructor(
    private val repository: DevicesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        repository.pairedDevices,
        repository.scannedDevices,
        _state
    ) { paired, scanned, oldState ->
        BluetoothUiState(paired, scanned)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    fun startScanning() = repository.discoverDevices()

    fun stopScanning() = repository.stopDiscoveringDevices()
}