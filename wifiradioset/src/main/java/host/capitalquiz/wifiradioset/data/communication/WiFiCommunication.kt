package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.RadioSetCommunication
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
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
) : RadioSetCommunication {
    private var connectionResultJob: Job? = null
    private var scope: CoroutineScope? = null
    private var socketHolder: RadioSetSocketHolder = RadioSetSocketHolder.Idle
    private val _connectionResult =
        MutableStateFlow<WiFiConnectionResult>(WiFiConnectionResult.Idle)
    override val connectionResult = _connectionResult.asStateFlow()

    override fun startServer(connectedDevice: WifiDevice) {
        socketHolder.close()
        val server = modeFactory.server(connectedDevice, PORT)
        socketHolder = server
        updateConnectionResult(server.connect())
    }

    override fun startClient(connectedDevice: WifiDevice, address: InetAddress) {
        socketHolder.close()
        val client = modeFactory.client(connectedDevice, address, PORT)
        socketHolder = client
        updateConnectionResult(client.connect())
    }

    private fun updateConnectionResult(result: Flow<WiFiConnectionResult>) {
        if (scope == null) scope = CoroutineScope(Dispatchers.Main)
        connectionResultJob?.cancel()
        connectionResultJob = scope?.launch {
            result.collect(_connectionResult::tryEmit)
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
            if (socketHolder.socket()?.isConnected != true) return@flow
            val buffer = ByteArray(1024)
            val inputStream = socketHolder.socket()!!.inputStream
            while (true) {
                try {
                    inputStream.read(buffer)
                } catch (e: Exception) {
                    break
                }
                emit(buffer.decodeToString())
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun stop() {
        _connectionResult.tryEmit(WiFiConnectionResult.Abort)
        scope?.cancel()
        scope = null
        connectionResultJob = null
        socketHolder.close()
    }
}