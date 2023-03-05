package net.pelsmaeker.katerm.io

import java.io.IOException

/**
 * Exception indicating that the requested resource was not found.
 */
class ResourceNotFoundException @JvmOverloads constructor(
    val resourcePath: String,
    message: String? = null,
    cause: Throwable? = null
): IOException(message ?: "The requested resource was not found at: $resourcePath", cause)
