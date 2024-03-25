package host.capitalquiz.wifiradioset.data.communication

import java.io.InputStream

interface SendAudio {
    suspend fun send(inputStream: InputStream)
}