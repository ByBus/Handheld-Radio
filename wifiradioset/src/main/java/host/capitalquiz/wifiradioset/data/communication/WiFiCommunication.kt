package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.RadioSetCommunication
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetAddress
import javax.inject.Inject


private const val PORT = 8888

class WiFiCommunication @Inject constructor(
    private val modeFactory: RadioSetModeFactory,
    private val recorder: Recorder,
    private val audioPlayer: AudioPlayer,
) : RadioSetCommunication {
    private var connectionResultJob: Job? = null
    private var scope: CoroutineScope? = null
    private var socketHolder: RadioSetSocketHolder = RadioSetSocketHolder.Idle
    private val _connectionResult =
        MutableStateFlow<WiFiConnectionResult>(WiFiConnectionResult.Idle)
    override val connectionResult = _connectionResult.asStateFlow()

    @Volatile
    private var awaitConnection = CompletableDeferred<Unit>()

    override fun configureAsServer(connectedDevice: WifiDevice) {
        socketHolder.close()
        socketHolder = modeFactory.server(connectedDevice, PORT)
    }

    override fun configureAsClient(connectedDevice: WifiDevice, address: InetAddress) {
        socketHolder.close()
        socketHolder = modeFactory.client(connectedDevice, address, PORT)
    }

    override fun connect() {
        val connectionResultFlow = socketHolder.connect()
        if (scope == null) scope = CoroutineScope(Dispatchers.Main)
        connectionResultJob?.cancel()
        connectionResultJob = scope?.launch {
            connectionResultFlow
                .onEach { if (it.isSuccessConnection) awaitConnection.complete(Unit) }
                .collect(_connectionResult::tryEmit)
        }
    }

    override suspend fun recordAudio() {
        awaitConnection.await()
        val socket = socketHolder.socket()
        if (socket?.isConnected != true) return
        try {
            recorder.record(socket.outputStream)
        } catch (e: IOException) {
            _connectionResult.tryEmit(WiFiConnectionResult.Abort)
        }
    }

    override suspend fun listen() {
        awaitConnection.await()
        val socket = socketHolder.socket()
        if (socket?.isConnected != true) return
        try {
            audioPlayer.play(socket.inputStream)
        } catch (e: IOException) {
            _connectionResult.tryEmit(WiFiConnectionResult.Abort)
        }
    }

    override suspend fun mute(disableSound: Boolean) {
        recorder.tryPause(disableSound.not())
        audioPlayer.pause(disableSound)
    }

    override fun stop() {
        awaitConnection = CompletableDeferred()
        audioPlayer.stop()
        recorder.stop()
        _connectionResult.tryEmit(WiFiConnectionResult.Abort)
        scope?.cancel()
        scope = null
        connectionResultJob = null
        socketHolder.close()
    }
}