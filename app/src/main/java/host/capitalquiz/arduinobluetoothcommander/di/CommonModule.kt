package host.capitalquiz.arduinobluetoothcommander.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import host.capitalquiz.common.di.DispatcherDefault
import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.common.di.DispatcherMain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {
    companion object {
        @DispatcherIO
        @Provides
        fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

        @DispatcherDefault
        @Provides
        fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

        @DispatcherMain
        @Provides
        fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main
    }
}