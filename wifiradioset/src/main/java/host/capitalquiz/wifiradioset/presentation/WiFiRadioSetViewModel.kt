package host.capitalquiz.wifiradioset.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.wifiradioset.domain.RadioSetCommunication
import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WiFiRadioSetViewModel @Inject constructor(
    private val communication: RadioSetCommunication,
    private val wifiRepository: RadioSetRepository,
) : ViewModel() {
    val devices = wifiRepository.devices
    val wifiState = wifiRepository.wifiState
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun findDevices() = wifiRepository.startDeviceDiscovering()

    fun connect(device: WifiDevice) = wifiRepository.connect(device)


    fun receiveMessages() {
        viewModelScope.launch {
            communication.receive().collect {
                _message.value = it
            }
            delay(100)
            _message.value = null
        }
    }

    override fun onCleared() {
        wifiRepository.close()
        super.onCleared()
    }

    fun send(message: String) {
        viewModelScope.launch {
            communication.send(message)
        }
    }
}