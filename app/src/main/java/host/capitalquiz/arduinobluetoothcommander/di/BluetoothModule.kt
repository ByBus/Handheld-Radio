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
import host.capitalquiz.arduinobluetoothcommander.data.BluetoothDevicesRepository
import host.capitalquiz.arduinobluetoothcommander.data.DevicesClosableDataSource
import host.capitalquiz.arduinobluetoothcommander.data.FoundDevicesReceiver
import host.capitalquiz.arduinobluetoothcommander.data.PairedDevicesDataSource
import host.capitalquiz.arduinobluetoothcommander.domain.DevicesRepository
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

    companion object {
        @Singleton
        @Provides
        fun provideBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager? {
            return appContext.getSystemService()
        }
    }
}