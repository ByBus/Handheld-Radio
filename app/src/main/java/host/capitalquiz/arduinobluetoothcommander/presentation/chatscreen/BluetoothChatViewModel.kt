package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Message
import host.capitalquiz.arduinobluetoothcommander.domain.MessageMapper
import host.capitalquiz.arduinobluetoothcommander.domain.MessagesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BluetoothChatViewModel.Factory::class)
class BluetoothChatViewModel @AssistedInject constructor(
    @Assisted("name") val chatName: String,
    @Assisted("mac") val macAddress: String,
    private val communication: Communication,
    private val messagesRepository: MessagesRepository,
    private val connectionResultMapper: ConnectionResult.Mapper<ChatConnectionUi>,
    private val messageMapper: MessageMapper<MessageUi>,
) : ViewModel() {

    val uiState = combine(
        messagesRepository.readMessages(macAddress, chatName)
            .map { messages -> messages.map { it.map(messageMapper) } },
        communication.connectionState.map { it.map(connectionResultMapper) }
    ) { messages, connectionState ->
        ChatUiState(chatName, messages, connectionState.isConnected())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState(chatName))

    init {
        viewModelScope.launch {
            messagesRepository.receiveIncomingMessages().collect()
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            messagesRepository.send(Message(text = text))
        }
    }

    override fun onCleared() {
        communication.close()
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("name") chatName: String,
            @Assisted("mac") macAddress: String,
        ): BluetoothChatViewModel
    }
}