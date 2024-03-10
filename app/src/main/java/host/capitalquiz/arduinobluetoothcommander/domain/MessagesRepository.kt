package host.capitalquiz.arduinobluetoothcommander.domain

import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun readMessages(mac: String, newName: String): Flow<List<Message>>

    suspend fun send(message: Message): Long

    fun receiveIncomingMessages(): Flow<Message>
}