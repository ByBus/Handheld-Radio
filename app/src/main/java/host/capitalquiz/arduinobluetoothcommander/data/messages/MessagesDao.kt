package host.capitalquiz.arduinobluetoothcommander.data.messages

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId")
    fun messages(chatId: Long): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(messageEntity: MessageEntity): Long

    @Query("SELECT * FROM chats WHERE mac = :mac")
    suspend fun findChatByMac(mac: String): ChatWithMessages?

    @Insert
    suspend fun insert(chat: ChatEntity): Long
}