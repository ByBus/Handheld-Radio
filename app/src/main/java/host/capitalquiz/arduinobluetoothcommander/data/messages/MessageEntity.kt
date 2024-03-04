package host.capitalquiz.arduinobluetoothcommander.data.messages

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import host.capitalquiz.arduinobluetoothcommander.domain.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "chatId")
    val chatId: Long,
    @ColumnInfo(name = "from_me")
    val fromMe: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun toMessage(): Message = Message(id, name, text, date, chatId, fromMe)
}