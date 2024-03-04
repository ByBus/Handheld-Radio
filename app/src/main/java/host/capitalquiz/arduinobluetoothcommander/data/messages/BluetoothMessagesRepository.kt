package host.capitalquiz.arduinobluetoothcommander.data.messages

import host.capitalquiz.arduinobluetoothcommander.data.toBluetoothMessage
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.Chat
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.Message
import host.capitalquiz.arduinobluetoothcommander.domain.MessagesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BluetoothMessagesRepository @Inject constructor(
    private val communication: Communication,
    private val messagesLocalDataSource: MessagesDataSource,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : MessagesRepository {
    private var currentChatId: Long = 0

    override suspend fun readMessages(from: String): Flow<List<Message>> {
        return withContext(dispatcher) {
            currentChatId =
                messagesLocalDataSource.findChatByName(from)?.id ?: messagesLocalDataSource.create(
                    Chat(-1, from, System.currentTimeMillis())
                )
            messagesLocalDataSource.getMessages(currentChatId).flowOn(dispatcher)
        }
    }

    override suspend fun send(message: Message): Long {
        val newMessage = message.copy(chatId = currentChatId, fromMe = true)
        val result = communication.send(newMessage.toBluetoothMessage())
        return if (result) messagesLocalDataSource.insert(message) else 0
    }

    override fun receiveIncomingMessages(): Flow<Message> {
        return communication.receive().map { btMessage ->
            Message(-1, btMessage.name, btMessage.message, btMessage.time, currentChatId)
        }.onEach { message ->
            messagesLocalDataSource.insert(message)
        }
    }
}