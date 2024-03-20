package host.capitalquiz.wifiradioset.data.communication

import java.net.Socket

interface RadioSetSocketHolder {

    fun socket(): Socket?

    fun close()

    object Idle : RadioSetSocketHolder {
        override fun socket(): Socket? = null
        override fun close() = Unit
    }
}