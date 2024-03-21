package host.capitalquiz.wifiradioset.presentation.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.wifiradioset.domain.Communication
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.presentation.devices.WiFiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias ConnUiResultUi = WiFiConnectionUiResult
typealias ConnResultUiMapper<R> = WiFiConnectionResult.Mapper<R>

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val communication: Communication,
    private val connectionResultUiMapper: ConnResultUiMapper<ConnUiResultUi>,
) : ViewModel() {
    private val _event = Channel<WiFiEvent>()
    val event = _event.receiveAsFlow()

    private val state = communication.connectionResult

    init {
        communication.connect()
        viewModelScope.launch {
            communication.receive().collect {
                _event.trySend(WiFiEvent.Toast(it))
            }
        }
        viewModelScope.launch {
            state.collect {
                _event.trySend(it.map(connectionResultUiMapper).produceEvent())
            }
        }
    }

    fun send(text: String) {
        viewModelScope.launch {
            communication.send(text)
        }
    }

    override fun onCleared() {
        communication.stop()
        super.onCleared()
    }
}