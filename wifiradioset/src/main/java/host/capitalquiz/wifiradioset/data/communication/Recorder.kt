package host.capitalquiz.wifiradioset.data.communication

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import androidx.annotation.RawRes
import dagger.hilt.android.qualifiers.ApplicationContext
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


interface Recorder : SendAudio {
    suspend fun record(outputStream: OutputStream)
    fun tryPause(isPaused: Boolean)
    fun stop()

    class Microphone @Inject constructor(
        private val sampleRate: Int = 44100,
        private val channelConfig: Int = AudioFormat.CHANNEL_IN_STEREO,
        private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        @ApplicationContext private val context: Context,
        @RawRes private val overAudioFile: Int,
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

        private val inputAudio = Channel<InputStream>()

        override suspend fun record(outputStream: OutputStream) {
            microphone = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize
            ).apply {
                isRecording = true
                startRecording()
                suppressNoise(audioSessionId)
            }

            val buffer = ByteArray(minBufferSize)
            withContext(dispatcher) {
                launch {
                    while (coroutineContext.isActive) {
                        inputAudio.receive().writeTo(outputStream, buffer)
                    }
                }
                while (coroutineContext.isActive && isRecording) {
                    if (isPaused.get()) {
                        context.resources
                            .openRawResource(overAudioFile)
                            .writeTo(outputStream, buffer)
                        pauseDeferred.await()
                    }
                    val readSize = microphone!!.read(buffer, 0, buffer.size)
                    if (readSize < 0) break
                    outputStream.write(buffer, 0, buffer.size)
                    if (pauseDeferred.isCompleted) pauseDeferred = CompletableDeferred()
                }
            }
        }

        private fun suppressNoise(audioSessionId: Int) {
            if (NoiseSuppressor.isAvailable()) {
                NoiseSuppressor.create(audioSessionId).apply {
                    enabled = true
                }
            }
        }

        private fun InputStream.writeTo(
            outPutStream: OutputStream,
            buffer: ByteArray,
        ) {
            use { inputStream ->
                inputStream.skip(100) // skip wav header
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    if (bytesRead < buffer.size) break
                    outPutStream.write(buffer, 0, buffer.size)
                }
            }
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
            if (microphone?.state == AudioRecord.STATE_INITIALIZED) {
                microphone?.stop()
                microphone?.release()
            }
            microphone = null
        }

        override suspend fun send(inputStream: InputStream) = inputAudio.send(inputStream)
    }
}