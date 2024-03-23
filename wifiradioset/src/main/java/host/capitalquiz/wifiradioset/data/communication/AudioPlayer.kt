package host.capitalquiz.wifiradioset.data.communication

import android.R.attr.streamType
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject


interface AudioPlayer {
    suspend fun play(inputStream: InputStream)
    fun pause(isPause: Boolean)
    fun stop()

    class StreamAudioPlayer @Inject constructor(
        private val sampleRate: Int = 44100,
        private val channelConfig: Int = AudioFormat.CHANNEL_IN_STEREO,
        private val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : AudioPlayer {
        private val minBufferSize =
            AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        private var player: AudioTrack? = null

        override suspend fun play(inputStream: InputStream) {
            val audioBuffer = ByteArray(minBufferSize)
            player = AudioTrack().apply {
                play()
            }
            withContext(dispatcher) {
                try {
                    while (coroutineContext.isActive && inputStream.read(audioBuffer) != -1) {
                        if (player?.state == AudioTrack.PLAYSTATE_PAUSED) continue
                        player?.write(audioBuffer, 0, audioBuffer.size)
                    }
                } finally {
                    stop()
                }
            }
        }

        override fun pause(isPause: Boolean) {
            player?.let {
                val playState = it.playState
                val shouldPause = playState == AudioTrack.PLAYSTATE_PLAYING && isPause
                val shouldPlay = playState == AudioTrack.PLAYSTATE_PAUSED && isPause.not()
                when {
                    shouldPause -> it.pause()
                    shouldPlay -> it.play()
                }
            }
        }

        override fun stop() {
            try {
                player?.flush()
                player?.stop()
                player?.release()
            } catch (_: Exception) {
            } finally {
                player = null
            }
        }


        private fun AudioTrack(): AudioTrack {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(minBufferSize)
                    .build()
            } else {
                AudioTrack(
                    streamType,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize,
                    AudioTrack.MODE_STREAM
                )
            }
        }
    }
}