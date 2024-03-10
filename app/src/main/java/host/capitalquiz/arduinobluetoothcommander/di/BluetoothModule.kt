package host.capitalquiz.arduinobluetoothcommander.di

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.data.communication.BluetoothDeviceStateReceiver
import host.capitalquiz.arduinobluetoothcommander.data.communication.BluetoothMessageDecoder
import host.capitalquiz.arduinobluetoothcommander.data.communication.ConnectionModeFactory
import host.capitalquiz.arduinobluetoothcommander.data.communication.DeviceConnectionWatcher
import host.capitalquiz.arduinobluetoothcommander.data.communication.DevicesCommunication
import host.capitalquiz.arduinobluetoothcommander.data.devices.BluetoothDevicesRepository
import host.capitalquiz.arduinobluetoothcommander.data.devices.BluetoothStatus
import host.capitalquiz.arduinobluetoothcommander.data.devices.DeviceNameProvider
import host.capitalquiz.arduinobluetoothcommander.data.devices.DevicesClosableDataSource
import host.capitalquiz.arduinobluetoothcommander.data.devices.FoundDevicesReceiver
import host.capitalquiz.arduinobluetoothcommander.data.devices.PairedDevicesDataSource
import host.capitalquiz.arduinobluetoothcommander.data.messages.MessagesDatabase
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothChecker
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult.*
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ConnectionResultUi
import host.capitalquiz.arduinobluetoothcommander.presentation.ResourceProvider
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.ConnectionResultToUiMapper
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.DeviceUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BluetoothModule {

    @PairedDevices
    @Binds
    fun bindPairedDevicesDataSource(impl: PairedDevicesDataSource): DevicesClosableDataSource

    @ScannedDevices
    @Binds
    @Singleton
    fun bindScannedDevicesDataSource(impl: FoundDevicesReceiver): DevicesClosableDataSource

    @Binds
    fun bindBluetoothDevicesRepository(impl: BluetoothDevicesRepository): DevicesRepository

    @Binds
    fun bindBluetoothStatus(impl: BluetoothStatus): BluetoothChecker

    @Binds
    @Singleton
    fun bindDevicesCommunication(impl: DevicesCommunication): Communication

    @Binds
    fun provideDeviceConnectionWatcher(impl: BluetoothDeviceStateReceiver): DeviceConnectionWatcher

    @Binds
    fun bindBluetoothMessageDecoder(impl: BluetoothMessageDecoder.Base): BluetoothMessageDecoder

    @Binds
    fun bindConnectionModeFactory(impl: ConnectionModeFactory.Base): ConnectionModeFactory

    @Binds
    fun bindDeviceNameProvider(impl: DeviceNameProvider.BluetoothName): DeviceNameProvider

    @Binds
    fun bindConnectionResultToUiMapper(impl: ConnectionResultToUiMapper): Mapper<ConnectionResultUi>

    @Binds
    fun bindStringResourceProvider(impl: ResourceProvider.StringProvider): ResourceProvider<String>

    companion object {
        @Singleton
        @Provides
        fun provideBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager? {
            return appContext.getSystemService()
        }

        @DispatcherIO
        @Provides
        fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        fun provideUiStateMapper(@ApplicationContext context: Context): DeviceMapper<DeviceUi> {
            val defaultName = context.resources.getString(R.string.default_device_name)
            return DeviceMapper { name, macAddress ->
                DeviceUi(name ?: defaultName, macAddress)
            }
        }

        @Provides
        @Singleton
        fun provideMessagesDatabase(@ApplicationContext context: Context): MessagesDatabase {
            return Room.databaseBuilder(context, MessagesDatabase::class.java, "messages.db")
                .build()
        }
    }
}