package net.pelsmaeker.lsputils.diagnostics

/**
 * A diagnostic message (e.g., parse error, type error, deprecation warning, etc.).
 *
 * @property severity The severity of the message.
 * @property text The message text.
 * @property resource The resource ID of the source file where the message occurred, if known; otherwise, `null`.
 * @property span The text span where the message occurred, if known; otherwise, `null`.
 * @property sourceName A human-readable description of the part of the pipeline that generated the message, e.g. 'parser' or 'typechecker', if known; otherwise, `null`.
 */
data class Message(
    val severity: MessageSeverity,
    val text: String,
    val resource: ResourceID?,
    val span: TextSpan?,
    val sourceName: String?,
) {
    /** Whether the message is a fatal message. */
    val isFatal: Boolean get() = severity == MessageSeverity.FATAL

    /** Whether the message is an error message. */
    val isError: Boolean get() = severity == MessageSeverity.ERROR

    /** Whether the message is a warning message. */
    val isWarning: Boolean get() = severity == MessageSeverity.WARNING
}

