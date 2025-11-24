package net.pelsmaeker.katerm.generator.diagnostics

import net.pelsmaeker.katerm.generator.text.TextOrigin

/**
 * A diagnostic message (e.g., parse error, type error, deprecation warning, etc.).
 *
 * @property severity The severity of the message.
 * @property text The message text.
 * @property origin The origin of where the message occurred, if known; otherwise, `null`.

 * @property sourceName A human-readable description of the part of the pipeline that generated the message,
 * e.g. 'parser' or 'typechecker', if known; otherwise, `null`.
 */
data class Message(
    val severity: MessageSeverity,
    val text: String,
    val origin: TextOrigin?,
    val sourceName: String?,
) {
    /** Whether the message is a fatal message. */
    val isFatal: Boolean get() = severity == MessageSeverity.FATAL

    /** Whether the message is an error message. */
    val isError: Boolean get() = severity == MessageSeverity.ERROR

    /** Whether the message is a warning message. */
    val isWarning: Boolean get() = severity == MessageSeverity.WARNING
}

