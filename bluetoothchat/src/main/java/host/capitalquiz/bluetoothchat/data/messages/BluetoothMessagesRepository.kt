package host.capitalquiz.bluetoothchat.data.messages

import host.capitalquiz.bluetoothchat.data.devices.DeviceNameProvider
import host.capitalquiz.bluetoothchat.data.toBluetoothMessage
import host.capitalquiz.bluetoothchat.domain.chat.Chat
import host.capitalquiz.bluetoothchat.domain.Communication
import host.capitalquiz.bluetoothchat.domain.InstanceProvider
import host.capitalquiz.bluetoothchat.domain.chat.Message
import host.capitalquiz.bluetoothchat.domain.chat.MessagesRepository
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BluetoothMessagesRepository @Inject constructor(
    private val communicationProvider: InstanceProvider<Communication>,
    private val messagesLocalDataSource: MessagesDataSource,
    private val nameProvider: DeviceNameProvider,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : MessagesRepository {
    private var currentChatId: Long = 0

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun readMessages(mac: String, newName: String): Flow<List<Message>> = flow {
        currentChatId =
            messagesLocalDataSource.findChatByMac(mac)?.id ?: messagesLocalDataSource.create(
                Chat(-125, newName, mac, System.currentTimeMillis())
            )
        emit(currentChatId)
    }.flatMapLatest { messagesLocalDataSource.getMessages(it) }.flowOn(dispatcher)

    override suspend fun send(message: Message): Long {
        val newMessage =
            message.copy(name = nameProvider.provide(), chatId = currentChatId, fromMe = true)
        val result = communicationProvider.provide().send(newMessage.toBluetoothMessage())
        return if (result) messagesLocalDataSource.insert(newMessage) else 0
    }

    override fun receiveIncomingMessages(): Flow<Message> {
        return communicationProvider.provide().receive().map { btMessage ->
            Message(-1, btMessage.name, btMessage.message, btMessage.time, currentChatId)
        }.onEach { message ->
            messagesLocalDataSource.insert(message)
        }
    }
}