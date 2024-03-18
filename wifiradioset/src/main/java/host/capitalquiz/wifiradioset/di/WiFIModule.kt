package host.capitalquiz.wifiradioset.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.content.getSystemService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import host.capitalquiz.wifiradioset.data.ConnectionManager
import host.capitalquiz.wifiradioset.data.NetworkChecker
import host.capitalquiz.wifiradioset.data.WifiConnectionManager
import host.capitalquiz.wifiradioset.data.WifiRadioSetRepository
import host.capitalquiz.wifiradioset.domain.RadioSetRepository

@Module
@InstallIn(ViewModelComponent::class)
interface WiFIModule {

    @Binds
    fun bindWiFIConnectionChecker(impl: NetworkChecker.WiFiChecker): NetworkChecker

    @Binds
    fun bindWifiRadioSetRepository(impl: WifiRadioSetRepository): RadioSetRepository

    @Binds
    fun bindWifiConnectionManager(impl: WifiConnectionManager): ConnectionManager

    companion object {
        @Provides
        @ViewModelScoped
        fun provideWifiP2PManager(@ApplicationContext context: Context): WifiP2pManager =
            context.getSystemService()!!

        @Provides
        fun provideWifiManager(@ApplicationContext context: Context): WifiManager =
            context.getSystemService()!!

        @Provides
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
            context.getSystemService()!!
    }
}