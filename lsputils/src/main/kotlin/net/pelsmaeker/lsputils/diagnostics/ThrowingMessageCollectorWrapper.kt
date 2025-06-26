package net.pelsmaeker.lsputils.diagnostics

/**
 * A [MessageCollector] that stops processing messages after the first error and throws it.
 *
 * @property collector The underlying message collector.
 */
class ThrowingMessageCollectorWrapper(
    val collector: MessageCollector,
): MessageCollector {

    /** Whether the collector has collected an error. */
    var hasError: Boolean = false
        private set

    override fun offer(message: Message): Boolean {
        if (hasError) return false
        collector.offer(message)
        hasError = hasError || (message.severity >= MessageSeverity.ERROR)
        if (message.severity >= MessageSeverity.FATAL) {
            throw RuntimeException("Fatal error: ${message.text}")
        } else if (message.severity >= MessageSeverity.ERROR) {
            throw RuntimeException("Error: ${message.text}")
        }
        return false
    }
}