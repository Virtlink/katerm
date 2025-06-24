package net.pelsmaeker.lsputils.diagnostics

/**
 * A [MessageCollector] that stops processing messages after the first error.
 *
 * @property collector The underlying message collector.
 */
class FailFastMessageCollectorWrapper(
    val collector: MessageCollector,
): MessageCollector {

    /** Whether the collector has collected an error. */
    var hasError: Boolean = false
        private set

    override fun offer(message: Message): Boolean {
        if (hasError) return false
        collector.offer(message)
        hasError = hasError || (message.severity >= MessageSeverity.ERROR)
        return true
    }
}