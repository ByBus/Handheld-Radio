package host.capitalquiz.wifiradioset.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import javax.inject.Inject

@HiltViewModel
class WiFiRadioSetViewModel @Inject constructor(
    private val wifiRepository: RadioSetRepository,
) : ViewModel() {
    val devices = wifiRepository.devices
    val wifiState = wifiRepository.wifiState


    fun findDevices() = wifiRepository.startDeviceDiscovering()

    fun connect(device: WifiDevice) = wifiRepository.connect(device)

    override fun onCleared() {
        wifiRepository.close()
        super.onCleared()
    }
}