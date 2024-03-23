package host.capitalquiz.wifiradioset.data.communication

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


interface Recorder {
    suspend fun record(outputStream: OutputStream)
    fun tryPause(isPaused: Boolean)
    fun stop()

    class Microphone @Inject constructor(
        private val sampleRate: Int = 44100,
        private val channelConfig: Int = AudioFormat.CHANNEL_IN_STEREO,
        private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : Recorder {
        private val minBufferSize =
            AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        private var microphone: AudioRecord? = null
        private val isPaused = AtomicBoolean(false)

        @Volatile
        private var isRecording = false

        @Volatile
        private var pauseDeferred = CompletableDeferred<Unit>()

        override suspend fun record(outputStream: OutputStream) {
            stop()
            microphone = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize
            ).apply {
                isRecording = true
                startRecording()
            }
            val buffer = ByteArray(minBufferSize)
            withContext(dispatcher) {
                while (coroutineContext.isActive && isRecording) {
                    if (isPaused.get()) pauseDeferred.await()
                    val readSize = microphone!!.read(buffer, 0, buffer.size)
                    if (readSize < 0) break
                    outputStream.write(buffer, 0, buffer.size)
                    if (pauseDeferred.isCompleted) pauseDeferred = CompletableDeferred()
                }
            }
//            stop()
        }


        override fun tryPause(isPaused: Boolean) {
            this.isPaused.set(isPaused)
            if (isPaused.not()) {
                pauseDeferred.complete(Unit)
            }
        }

        override fun stop() {
            isPaused.set(false)
            isRecording = false
            microphone?.stop()
            microphone?.release()
            microphone = null
        }

    }
}