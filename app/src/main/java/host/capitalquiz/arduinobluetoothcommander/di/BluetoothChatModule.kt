package host.capitalquiz.arduinobluetoothcommander.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.data.messages.BluetoothMessagesRepository
import host.capitalquiz.arduinobluetoothcommander.data.messages.MessagesDao
import host.capitalquiz.arduinobluetoothcommander.data.messages.MessagesDataSource
import host.capitalquiz.arduinobluetoothcommander.data.messages.MessagesDatabase
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.MessageMapper
import host.capitalquiz.arduinobluetoothcommander.domain.MessagesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.ChatConnectionUi
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.CurrentTimeProvider
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.MessageUi
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.mappers.ChatConnectionResultToUiMapper
import host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen.mappers.MessageToUiMapper

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