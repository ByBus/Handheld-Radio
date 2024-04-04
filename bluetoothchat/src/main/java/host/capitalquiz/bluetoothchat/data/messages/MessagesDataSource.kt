package host.capitalquiz.bluetoothchat.data.messages

import host.capitalquiz.bluetoothchat.domain.chat.Chat
import host.capitalquiz.bluetoothchat.domain.chat.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MessagesDataSource {
    fun getMessages(chatId: Long): Flow<List<Message>>

    suspend fun insert(message: Message): Long

    suspend fun findChatByMac(mac: String): Chat?

    suspend fun create(chat: Chat): Long

    class Room @Inject constructor(private val dao: MessagesDao) : MessagesDataSource {

        override fun getMessages(chatId: Long): Flow<List<Message>> {
            return dao.messages(chatId).map { dbMessages ->
                dbMessages.map {
                    Message(it.id, it.name, it.text, it.date, it.chatId, it.fromMe)
                }
            }
        }

        override suspend fun insert(message: Message): Long {
            with(message) {
                return dao.insert(MessageEntity(name, date, text, chatId, fromMe))
            }
        }

        override suspend fun findChatByMac(mac: String): Chat? {
            return dao.findChatByMac(mac)?.let { dbChat ->
                Chat(
                    dbChat.chat.id,
                    dbChat.chat.name,
                    dbChat.chat.mac,
                    dbChat.chat.date,
                    dbChat.messages.map { it.toMessage() })
            }
        }

        override suspend fun create(chat: Chat): Long {
            return dao.insert(ChatEntity(chat.name, chat.mac, chat.date))
        }
    }
}