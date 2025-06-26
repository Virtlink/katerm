package net.pelsmaeker.lsputils.diagnostics

/**
 * A diagnostic message (e.g., parse error, type error, deprecation warning, etc.).
 *
 * @property severity The severity of the message.
 * @property text The message text.
 * @property path The source file where the message occurred, if known; otherwise, `null`.
 * @property span The text span where the message occurred, if known; otherwise, `null`.
 */
data class Message(
    val severity: MessageSeverity,
    val text: String,
    val path: String?,
    val span: TextSpan?,
) {
    /** Whether the message is a fatal message. */
    val isFatal: Boolean get() = severity == MessageSeverity.FATAL

    /** Whether the message is an error message. */
    val isError: Boolean get() = severity == MessageSeverity.ERROR

    /** Whether the message is a warning message. */
    val isWarning: Boolean get() = severity == MessageSeverity.WARNING
}

