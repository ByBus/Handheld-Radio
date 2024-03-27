package host.capitalquiz.wifiradioset.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WiFiRadioSetViewModel @Inject constructor(
    private val wifiRepository: RadioSetRepository,
    private val wifiStateUiMapper: WifiState.Mapper<WifiStateUi>,
) : ViewModel() {
    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()
    private val baseState = MutableStateFlow(RadioSetUiState())
    val uiState = baseState.asStateFlow()

    init {
        viewModelScope.launch {
            wifiRepository.wifiState.collect {
                reduceUiState(it.map(wifiStateUiMapper))
            }
        }
    }

    private fun reduceUiState(wifiStateUi: WifiStateUi) {
        baseState.update {
            eventChannel.trySend(wifiStateUi.produceEvent())
            wifiStateUi.reduce(it)
        }
    }

    fun findDevices() = wifiRepository.startDeviceDiscovering()

    fun connect(device: WifiDevice) = wifiRepository.connect(device)

    override fun onCleared() {
        wifiRepository.close()
        super.onCleared()
    }

    fun disconnect() = wifiRepository.disconnect()
}