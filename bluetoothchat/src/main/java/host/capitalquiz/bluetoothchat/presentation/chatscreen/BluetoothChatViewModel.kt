package host.capitalquiz.bluetoothchat.presentation.chatscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import host.capitalquiz.bluetoothchat.domain.Communication
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.InstanceProvider
import host.capitalquiz.bluetoothchat.domain.chat.Message
import host.capitalquiz.bluetoothchat.domain.chat.MessageMapper
import host.capitalquiz.bluetoothchat.domain.chat.MessagesRepository
import host.capitalquiz.bluetoothchat.domain.chat.mapItems
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
    private val communication: InstanceProvider<Communication>,
    private val messagesRepository: MessagesRepository,
    private val connectionResultMapper: ConnectionResult.Mapper<ChatConnectionUi>,
    private val messageMapper: MessageMapper<MessageUi>,
) : ViewModel() {

    val uiState = combine(
        messagesRepository.readMessages(macAddress, chatName)
            .map { messages -> messages.mapItems(messageMapper) },
        communication.provide().connectionState.map { it.map(connectionResultMapper) }
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
        communication.provide().close()
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