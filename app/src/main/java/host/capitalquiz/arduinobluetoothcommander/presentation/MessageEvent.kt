package host.capitalquiz.arduinobluetoothcommander.presentation

sealed interface MessageEvent {
    fun consume(block: (String) -> Unit)

    object Empty : MessageEvent {
        override fun consume(block: (String) -> Unit) = Unit
    }

    data class Text(
        private val message: String,
        private val id: Long = System.currentTimeMillis(),
    ) : MessageEvent {
        private var consumed = false
        override fun consume(block: (String) -> Unit) {
            if (consumed.not()) {
                consumed = true
                block.invoke(message)
            }
        }
    }
}