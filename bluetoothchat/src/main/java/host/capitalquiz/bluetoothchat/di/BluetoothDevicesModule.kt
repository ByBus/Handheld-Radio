package host.capitalquiz.bluetoothchat.di

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
import host.capitalquiz.bluetoothchat.R
import host.capitalquiz.bluetoothchat.data.communication.BluetoothDeviceStateReceiver
import host.capitalquiz.bluetoothchat.data.communication.BluetoothMessageDecoder
import host.capitalquiz.bluetoothchat.data.communication.CommunicationSingletonFactory
import host.capitalquiz.bluetoothchat.data.communication.ConnectionModeFactory
import host.capitalquiz.bluetoothchat.data.communication.DeviceConnectionWatcher
import host.capitalquiz.bluetoothchat.data.communication.DevicesCommunication
import host.capitalquiz.bluetoothchat.data.devices.BluetoothDevicesRepository
import host.capitalquiz.bluetoothchat.data.devices.DeviceNameProvider
import host.capitalquiz.bluetoothchat.data.devices.DevicesClosableDataSource
import host.capitalquiz.bluetoothchat.data.devices.PairedDevicesDataSource
import host.capitalquiz.bluetoothchat.data.devices.ScannedDevicesReceiver
import host.capitalquiz.bluetoothchat.data.messages.MessagesDatabase
import host.capitalquiz.bluetoothchat.domain.Communication
import host.capitalquiz.bluetoothchat.domain.ConnectionResult.Mapper
import host.capitalquiz.bluetoothchat.domain.devices.DeviceMapper
import host.capitalquiz.bluetoothchat.domain.devices.DevicesRepository
import host.capitalquiz.bluetoothchat.domain.InstanceProvider
import host.capitalquiz.bluetoothchat.domain.SingletonFactory
import host.capitalquiz.bluetoothchat.presentation.ConnectionResultUi
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.ConnectionResultToUiMapper
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.DeviceUi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BluetoothDevicesModule {

    @PairedDevices
    @Binds
    fun bindPairedDevicesDataSource(impl: PairedDevicesDataSource): DevicesClosableDataSource

    @ScannedDevices
    @Binds
    @Singleton
    fun bindScannedDevicesDataSource(impl: ScannedDevicesReceiver): DevicesClosableDataSource

    @Binds
    fun bindBluetoothDevicesRepository(impl: BluetoothDevicesRepository): DevicesRepository

    @Binds
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
    @Singleton
    fun bindCommunicationFactory(impl: CommunicationSingletonFactory): SingletonFactory<Communication>


    companion object {
        @Provides
        fun provideCommunicationProvider(provider: SingletonFactory<Communication>): InstanceProvider<Communication> =
            provider

        @Provides
        fun provideBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager? {
            return appContext.getSystemService()
        }

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
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}