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
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.data.ConnectionManager
import host.capitalquiz.wifiradioset.data.NetworkChecker
import host.capitalquiz.wifiradioset.data.WifiConnectionManager
import host.capitalquiz.wifiradioset.data.WifiRadioSetRepository
import host.capitalquiz.wifiradioset.data.communication.AudioPlayer
import host.capitalquiz.wifiradioset.data.communication.AudioSessionFrequenciesProvider
import host.capitalquiz.wifiradioset.data.communication.RadioSetModeFactory
import host.capitalquiz.wifiradioset.data.communication.Recorder
import host.capitalquiz.wifiradioset.data.communication.WiFiCommunication
import host.capitalquiz.wifiradioset.domain.Communication
import host.capitalquiz.wifiradioset.domain.CommunicationMode
import host.capitalquiz.wifiradioset.domain.RadioSetCommunication
import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.VisualisationProvider
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiState
import host.capitalquiz.wifiradioset.presentation.conversation.WiFiConnectionUiResult
import host.capitalquiz.wifiradioset.presentation.conversation.WiFiConnectionUiResultMapper
import host.capitalquiz.wifiradioset.presentation.devices.WifiStateUi
import host.capitalquiz.wifiradioset.presentation.devices.WifiStateUiMapper
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
interface WiFIModule {

    @Binds
    fun bindWiFIConnectionChecker(impl: NetworkChecker.WiFiChecker): NetworkChecker

    @Binds
    fun bindWifiRadioSetRepository(impl: WifiRadioSetRepository): RadioSetRepository

    @Binds
    fun bindWifiConnectionManager(impl: WifiConnectionManager): ConnectionManager

    @Binds
    fun bindWiFiStateUiMapper(impl: WifiStateUiMapper): WifiState.Mapper<WifiStateUi>

    @Binds
    fun bindWiFiConnectionMapper(impl: WiFiConnectionUiResultMapper): WiFiConnectionResult.Mapper<WiFiConnectionUiResult>

    @Binds
    fun bindAudioVisualisationDataProvider(impl: AudioSessionFrequenciesProvider): VisualisationProvider

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

@Module
@InstallIn(ActivityRetainedComponent::class)
interface WiFiSingletonsModule {

    @Binds
    fun bindWiFiModeFactory(impl: RadioSetModeFactory.Wifi): RadioSetModeFactory

    @Binds
    @ActivityRetainedScoped
    fun bindWiFiCommunication(impl: WiFiCommunication): RadioSetCommunication

    companion object {
        @Provides
        fun provideConnectionModeConfigurator(communication: RadioSetCommunication): CommunicationMode =
            communication

        @Provides
        fun provideWiFiConnection(communication: RadioSetCommunication): Communication =
            communication

        @Provides
        fun provideRecorder(
            @DispatcherIO dispatcher: CoroutineDispatcher,
            @ApplicationContext context: Context,
        ): Recorder {
            return Recorder.Microphone(
                dispatcher = dispatcher,
                context = context,
                overAudioFile = R.raw.walkie_talkie_over_beep_44khz
            )
        }

        @Provides
        fun provideAudioPlayer(
            @DispatcherIO dispatcher: CoroutineDispatcher,

            ): AudioPlayer {
            return AudioPlayer.StreamAudioPlayer(
                dispatcher = dispatcher,
            )
        }
    }
}