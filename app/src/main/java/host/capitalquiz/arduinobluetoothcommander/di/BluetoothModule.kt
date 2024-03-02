package host.capitalquiz.arduinobluetoothcommander.di

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.data.BluetoothDeviceStateReceiver
import host.capitalquiz.arduinobluetoothcommander.data.BluetoothDevicesRepository
import host.capitalquiz.arduinobluetoothcommander.data.BluetoothMessageDecoder
import host.capitalquiz.arduinobluetoothcommander.data.BluetoothStatus
import host.capitalquiz.arduinobluetoothcommander.data.ConnectionModeFactory
import host.capitalquiz.arduinobluetoothcommander.data.DeviceConnectionWatcher
import host.capitalquiz.arduinobluetoothcommander.data.DevicesClosableDataSource
import host.capitalquiz.arduinobluetoothcommander.data.DevicesCommunication
import host.capitalquiz.arduinobluetoothcommander.data.FoundDevicesReceiver
import host.capitalquiz.arduinobluetoothcommander.data.PairedDevicesDataSource
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothChecker
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.DeviceMapper
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
import host.capitalquiz.arduinobluetoothcommander.presentation.ConnectionResultUi
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi
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
    fun bindDevicesCommunication(impl: DevicesCommunication): Communication

    @Binds
    fun provideDeviceConnectionWatcher(impl: BluetoothDeviceStateReceiver): DeviceConnectionWatcher

    @Binds
    fun bindBluetoothMessageDecoder(impl: BluetoothMessageDecoder.Base): BluetoothMessageDecoder

    @Binds
    fun bindConnectionModeFactory(impl: ConnectionModeFactory.Base): ConnectionModeFactory

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
        fun provideConnectionResultToUiMapper(@ApplicationContext context: Context): ConnectionResult.Mapper<ConnectionResultUi> {
            return ConnectionResult.Mapper { result ->
                when (result) {
                    is ConnectionResult.Idle -> ConnectionResultUi.Idle
                    is ConnectionResult.Connected -> ConnectionResultUi.ConnectionEstablished
                    is ConnectionResult.Connect ->
                        ConnectionResultUi.DeviceConnected(
                            result.device,
                            context.getString(R.string.is_disconnected)
                        )

                    is ConnectionResult.Disconnect ->
                        ConnectionResultUi.DeviceDisconnected(
                            result.device,
                            context.getString(R.string.was_disconnected)
                        )

                    is ConnectionResult.Error -> ConnectionResultUi.Error(result.message)
                    is ConnectionResult.Connecting -> ConnectionResultUi.Connecting
                }
            }
        }
    }
}