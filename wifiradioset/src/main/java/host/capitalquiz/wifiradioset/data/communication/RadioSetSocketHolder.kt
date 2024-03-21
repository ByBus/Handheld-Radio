package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.net.Socket

interface RadioSetSocketHolder : Connectable {

    fun socket(): Socket?

    object Idle : RadioSetSocketHolder {
        override fun socket(): Socket? = null
        override fun close() = Unit
        override fun connect(): Flow<WiFiConnectionResult> = emptyFlow()
    }
}