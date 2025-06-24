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

}

fun MessageCollector.fatal(message: String, path: String? = null, span: TextSpan? = null) =
    offer(Message(MessageSeverity.FATAL, message, path, span))

fun MessageCollector.error(message: String, path: String? = null, span: TextSpan? = null) =
    offer(Message(MessageSeverity.ERROR, message, path, span))

fun MessageCollector.warning(message: String, path: String? = null, span: TextSpan? = null) =
    offer(Message(MessageSeverity.WARNING, message, path, span))

fun MessageCollector.info(message: String, path: String? = null, span: TextSpan? = null) =
    offer(Message(MessageSeverity.INFO, message, path, span))

fun MessageCollector.hint(message: String, path: String? = null, span: TextSpan? = null) =
    offer(Message(MessageSeverity.HINT, message, path, span))