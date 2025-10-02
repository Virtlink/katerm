package net.pelsmaeker.lsputils.diagnostics

/** Collects diagnostic messages. */
fun interface MessageCollector {

    /**
     * Offer a diagnostic message to this collector.
     *
     * @param message The message to offset.
     * @return `true` to continue; otherwise, `false` to stop processing.
     */
    fun offer(message: Message): Boolean

    fun fatal(message: String, resource: ResourceID? = null, span: TextSpan? = null) =
        offer(Message(MessageSeverity.FATAL, message, resource, span, null))

    fun error(message: String, resource: ResourceID? = null, span: TextSpan? = null) =
        offer(Message(MessageSeverity.ERROR, message, resource, span, null))

    fun warning(message: String, resource: ResourceID? = null, span: TextSpan? = null) =
        offer(Message(MessageSeverity.WARNING, message, resource, span, null))

    fun info(message: String, resource: ResourceID? = null, span: TextSpan? = null) =
        offer(Message(MessageSeverity.INFO, message, resource, span, null))

    fun hint(message: String, resource: ResourceID? = null, span: TextSpan? = null) =
        offer(Message(MessageSeverity.HINT, message, resource, span, null))

}
