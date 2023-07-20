package net.pelsmaeker.katerm.io

/** An exception indicating that a textual or binary format could not be interpreted or parsed correctly. */
class FormatException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
) : IllegalStateException(
    message ?: "A format exception occurred.",
    cause,
)