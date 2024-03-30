package host.capitalquiz.wifiradioset.presentation.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.wifiradioset.domain.Communication
import host.capitalquiz.wifiradioset.domain.VisualisationProvider
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.toMagnitudes
import host.capitalquiz.wifiradioset.presentation.devices.StreamEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

typealias ConnUiResultUi = WiFiConnectionUiResult
typealias ConnResultUiMapper<R> = WiFiConnectionResult.Mapper<R>

@HiltViewModel(assistedFactory = ConversationViewModel.Factory::class)
class ConversationViewModel @AssistedInject constructor(
    @Assisted initialState: ConversationUiState,
    private val communication: Communication,
    private val visualisationDataSource: VisualisationProvider,
    private val connectionResultUiMapper: ConnResultUiMapper<ConnUiResultUi>,
) : ViewModel() {
    private val _event = Channel<StreamEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    val uiState = initialState

    private val _frequencies = MutableStateFlow<List<Int>>(emptyList())
    val frequencies = _frequencies.asStateFlow()

    init {
        communication.connect()
    }

    fun connect() {
        viewModelScope.launch {
            communication.connectionResult.collect {
                _event.trySend(it.map(connectionResultUiMapper).produceEvent())
            }
        }
    }

    fun startConversation() {
        viewModelScope.launch {
            communication.playAudio()
        }
        viewModelScope.launch {
            communication.recordAudio()
        }
    }

    fun speak() {
        viewModelScope.launch {
            communication.mute(true)
        }
    }

    fun listen() {
        viewModelScope.launch {
            communication.mute(false)
        }
    }

    fun sendSignal(inputStream: InputStream) {
        viewModelScope.launch {
            communication.sendAudio(inputStream)
        }
    }

    fun startAudioVisualization(sessionId: Int) {
        viewModelScope.launch {
            visualisationDataSource.visualization(sessionId).collect { bytes ->
                _frequencies.update { bytes.toMagnitudes(100) }
            }
        }
    }

    override fun onCleared() {
        communication.stop()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: ConversationUiState): ConversationViewModel

        companion object {
            fun createUiState(
                deviceName: String,
                deviceMac: String,
                networkName: String,
            ): ConversationUiState = ConversationUiState(deviceName, deviceMac, networkName)
        }
    }
}