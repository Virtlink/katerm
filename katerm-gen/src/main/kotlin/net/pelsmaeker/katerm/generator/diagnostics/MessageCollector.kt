package net.pelsmaeker.katerm.generator.diagnostics

import net.pelsmaeker.katerm.generator.ResourceID
import net.pelsmaeker.katerm.generator.text.TextOrigin
import net.pelsmaeker.katerm.generator.text.TextRange

/** Collects diagnostic messages. */
fun interface MessageCollector {

    /**
     * Offer a diagnostic message to this collector.
     *
     * @param message The message to offset.
     * @return `true` to continue; otherwise, `false` to stop processing.
     */
    fun offer(message: Message): Boolean

    fun fatal(message: String, resource: ResourceID, range: TextRange? = null) =
        offer(Message(MessageSeverity.FATAL, message, TextOrigin(resource, range), null))

    fun error(message: String, resource: ResourceID, range: TextRange? = null) =
        offer(Message(MessageSeverity.ERROR, message, TextOrigin(resource, range), null))

    fun warning(message: String, resource: ResourceID, range: TextRange? = null) =
        offer(Message(MessageSeverity.WARNING, message, TextOrigin(resource, range), null))

    fun info(message: String, resource: ResourceID, range: TextRange? = null) =
        offer(Message(MessageSeverity.INFO, message, TextOrigin(resource, range), null))

    fun hint(message: String, resource: ResourceID, range: TextRange? = null) =
        offer(Message(MessageSeverity.HINT, message, TextOrigin(resource, range), null))

    fun fatal(message: String, origin: TextOrigin? = null) =
        offer(Message(MessageSeverity.FATAL, message, origin, null))

    fun error(message: String, origin: TextOrigin? = null) =
        offer(Message(MessageSeverity.ERROR, message, origin, null))

    fun warning(message: String, origin: TextOrigin? = null) =
        offer(Message(MessageSeverity.WARNING, message, origin, null))

    fun info(message: String, origin: TextOrigin? = null) =
        offer(Message(MessageSeverity.INFO, message, origin, null))

    fun hint(message: String, origin: TextOrigin? = null) =
        offer(Message(MessageSeverity.HINT, message, origin, null))

}
