package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.Flow
import kotlin.math.log10

interface VisualisationProvider {
    fun visualization(audioSessionId: Int): Flow<ByteArray>
}

fun ByteArray.toMagnitudes(multiplier: Int, shiftDistance: Int = 0): List<Int> {
    val result = (0 until size / 2).map {
        val magnitude = this[2 * it] * this[2 * it] + this[2 * it + 1] * this[2 * it + 1]
        if (magnitude <= 0)
            0
        else
            (multiplier * log10(magnitude.toDouble())).toInt().coerceAtLeast(0)

    }
    return if (shiftDistance % result.size == 0)
        result
    else
        result.subList(shiftDistance, result.size) + result.subList(0, shiftDistance)
}