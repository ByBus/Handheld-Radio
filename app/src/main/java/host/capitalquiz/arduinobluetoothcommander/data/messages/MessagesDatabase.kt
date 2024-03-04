package host.capitalquiz.arduinobluetoothcommander.data.messages

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 2, exportSchema = false)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun dao(): MessagesDao
}