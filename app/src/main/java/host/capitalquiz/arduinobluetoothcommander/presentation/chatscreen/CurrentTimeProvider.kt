package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

import javax.inject.Inject

interface CurrentTimeProvider {
    fun now(): Long

    class Base @Inject constructor() : CurrentTimeProvider {
        override fun now(): Long = System.currentTimeMillis()
    }
}