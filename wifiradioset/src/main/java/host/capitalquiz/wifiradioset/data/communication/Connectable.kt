package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import kotlinx.coroutines.flow.Flow

interface Connectable {
    fun connect(): Flow<WiFiConnectionResult>
    fun close()
}