package host.capitalquiz.bluetoothchat.data.messages

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 3, exportSchema = false)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun dao(): MessagesDao
}