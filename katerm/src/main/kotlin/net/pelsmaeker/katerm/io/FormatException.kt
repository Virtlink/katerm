package net.pelsmaeker.katerm.io

/**
 * A parse exception.
 */
class FormatException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
) : IllegalStateException(
    message ?: "A parse exception occurred.",
    cause,
)