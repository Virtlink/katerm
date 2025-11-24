package net.pelsmaeker.katerm.generator.diagnostics

/**
 * A wrapper for a [MessageCollector] that inspects the messages it receives
 * to see if there were any errors.
 */
class InspectingMessageCollector(
    private val innerCollector: MessageCollector,
): MessageCollector {

    private var hasErrors = false

    override fun offer(message: Message): Boolean {
        hasErrors = hasErrors || message.isError || message.isFatal
        return innerCollector.offer(message)
    }

    /**
     * Determines whether the collector has encountered any (fatal) errors.
     *
     * @return `true` if there were any errors; otherwise, `false`.
     */
    fun hasErrors(): Boolean = hasErrors

}

/**
 * Creates an [InspectingMessageCollector] from the given [MessageCollector].
 *
 * @receiver The [MessageCollector] to wrap.
 * @return An [InspectingMessageCollector] that wraps the given [MessageCollector].
 */
fun MessageCollector.asInspectingMessageCollector(): InspectingMessageCollector {
    return this as? InspectingMessageCollector ?: InspectingMessageCollector(this)
}
