package net.pelsmaeker.lsputils.diagnostics

/**
 * A wrapper for a [MessageCollector] that sets the source on any messages it receives.
 */
class SetSourceMessageCollector(
    private val source: String,
    private val innerCollector: MessageCollector,
): MessageCollector {

    override fun offer(message: Message): Boolean {
        return innerCollector.offer(message.copy(sourceName = source))
    }

}
