package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.wifiradioset.domain.RadioSetCommunication
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject


private const val PORT = 8888

class WiFiCommunication @Inject constructor(
    private val modeFactory: RadioSetModeFactory,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : RadioSetCommunication {
    private var connectionResultJob: Job? = null
    private var scope: CoroutineScope? = null
    private var socketHolder: RadioSetSocketHolder = RadioSetSocketHolder.Idle
    private val _connectionResult =
        MutableStateFlow<WiFiConnectionResult>(WiFiConnectionResult.Idle)
    override val connectionResult = _connectionResult.asStateFlow()

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
            connectionResultFlow.collect(_connectionResult::tryEmit)
        }
    }

    override suspend fun send(message: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = message.encodeToByteArray()
                socketHolder.socket()?.outputStream?.write(bytes)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun receive(): Flow<String> {
        return flow {
            delay(2000)
            val socket = socketHolder.socket()
            if (socket?.isConnected != true) return@flow
            val buffer = ByteArray(1024)
            val inputStream = socket.inputStream
            var read: Int
            try {
                while (inputStream.read(buffer).also { read = it } != -1) {
                    if (read > 0) emit(buffer.decodeToString())
                }
            } catch (_: Exception) {
            } finally {
                stop()
            }
        }.flowOn(dispatcher)
    }

    override fun stop() {
        _connectionResult.tryEmit(WiFiConnectionResult.Abort)
        scope?.cancel()
        scope = null
        connectionResultJob = null
        socketHolder.close()
    }
}