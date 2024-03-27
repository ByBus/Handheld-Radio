package host.capitalquiz.bluetoothchat.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.bluetoothchat.R
import host.capitalquiz.bluetoothchat.data.messages.BluetoothMessagesRepository
import host.capitalquiz.bluetoothchat.data.messages.MessagesDao
import host.capitalquiz.bluetoothchat.data.messages.MessagesDataSource
import host.capitalquiz.bluetoothchat.data.messages.MessagesDatabase
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.MessageMapper
import host.capitalquiz.bluetoothchat.domain.MessagesRepository
import host.capitalquiz.bluetoothchat.presentation.chatscreen.ChatConnectionUi
import host.capitalquiz.bluetoothchat.presentation.chatscreen.CurrentTimeProvider
import host.capitalquiz.bluetoothchat.presentation.chatscreen.MessageUi
import host.capitalquiz.bluetoothchat.presentation.chatscreen.mappers.ChatConnectionResultToUiMapper
import host.capitalquiz.bluetoothchat.presentation.chatscreen.mappers.MessageToUiMapper

@Module
@InstallIn(ViewModelComponent::class)
interface BluetoothChatModule {

    @Binds
    fun bindConnectionResultToUiMapper(impl: ChatConnectionResultToUiMapper): ConnectionResult.Mapper<ChatConnectionUi>

    @Binds
    fun bindBluetoothChatRepository(impl: BluetoothMessagesRepository): MessagesRepository

    @Binds
    fun bindMessagesDataSource(impl: MessagesDataSource.Room): MessagesDataSource

    @Binds
    fun bindCurrentTomeProvider(impl: CurrentTimeProvider.Base): CurrentTimeProvider

    companion object {
        @Provides
        fun provideMessagesDao(database: MessagesDatabase): MessagesDao = database.dao()

        @Provides
        fun provideMessageToUiMapper(
            @ApplicationContext context: Context,
            timeProvider: CurrentTimeProvider,
        ): MessageMapper<MessageUi> {
            return MessageToUiMapper(
                context.getString(R.string.today),
                context.getString(R.string.yesterday),
                timeProvider
            )
        }
    }
}