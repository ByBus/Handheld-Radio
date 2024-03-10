package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

sealed interface Event {
    fun consume(block: (String) -> Unit)

    object Empty : Event {
        override fun consume(block: (String) -> Unit) = Unit
    }

    data class Text(
        private val message: String,
        private val id: Long = System.currentTimeMillis(),
    ) : Event {
        private var consumed = false
        override fun consume(block: (String) -> Unit) {
            if (consumed.not()) {
                consumed = true
                block.invoke(message)
            }
        }
    }
}