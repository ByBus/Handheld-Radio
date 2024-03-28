package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.Flow
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.roundToInt

interface VisualisationProvider {
    fun visualization(audioSessionId: Int): Flow<ByteArray>
}

fun ByteArray.toMagnitudes(multiplier: Int = 1, shiftDistance: Int = 0): List<Int> {
    val result = (0 until size / 2).map { k ->
        if (k == 0) abs(get(0).toInt())
        else {
            val i = k * 2
            val magnitude = hypot(get(i).toDouble(), get(i + 1).toDouble())
            val normalised = multiplier * log10(magnitude)
            normalised.roundToInt().coerceAtLeast(0)
        }
    }
    return if (shiftDistance % result.size == 0)
        result
    else
        result.subList(shiftDistance, result.size) + result.subList(0, shiftDistance)
}