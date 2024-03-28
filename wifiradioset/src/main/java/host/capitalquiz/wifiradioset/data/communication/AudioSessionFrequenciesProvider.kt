package host.capitalquiz.wifiradioset.data.communication

import android.media.audiofx.Visualizer
import host.capitalquiz.common.di.DispatcherDefault
import host.capitalquiz.wifiradioset.domain.VisualisationProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AudioSessionFrequenciesProvider @Inject constructor(
    @DispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : VisualisationProvider {
    override fun visualization(audioSessionId: Int): Flow<ByteArray> = callbackFlow {
        val visualizer = Visualizer(audioSessionId).apply {
            measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
            scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            captureSize = Visualizer.getCaptureSizeRange()[0]
        }
        val listener = object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer?,
                waveform: ByteArray?,
                samplingRate: Int,
            ) {
            }

            override fun onFftDataCapture(
                visualizer: Visualizer,
                fft: ByteArray,
                samplingRate: Int,
            ) {
                trySend(fft)
            }
        }
        visualizer.setDataCaptureListener(listener, Visualizer.getMaxCaptureRate(), false, true)
        visualizer.setEnabled(true)
        awaitClose {
            visualizer.enabled = false
            visualizer.release()
        }
    }.flowOn(dispatcher)
}