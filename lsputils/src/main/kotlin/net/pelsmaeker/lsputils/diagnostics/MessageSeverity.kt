package net.pelsmaeker.lsputils.diagnostics

/** Specifies the severity of a diagnostic message. */
enum class MessageSeverity {
    // These must be ordered from lowest to highest severity.

    /** The message is a hint. */
    HINT,
    /** The message is informational. */
    INFO,
    /** The message is a warning. */
    WARNING,
    /** The message is an error. */
    ERROR,
    /** The message is fatal and prevents the compiler from continuing. */
    FATAL,
}