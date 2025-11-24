package net.pelsmaeker.katerm.generator

import java.io.File
import java.nio.file.Path
import kotlin.text.replace
import kotlin.text.startsWith

/**
 * The identifier of a resource (document).
 *
 * This is typically a URI, but can also be constructed from a file path.
 *
 * @property uri The URI of the resource.
 */
@JvmInline
value class ResourceID private constructor(val uri: String): Comparable<ResourceID> {

    override fun compareTo(other: ResourceID): Int {
        return this.uri.compareTo(other.uri)
    }

    companion object {
        /**
         * Creates a [ResourceID] from the given file path.
         *
         * @param path The file path.
         * @return The resource ID.
         */
        fun fromPath(path: String): ResourceID {
            val normalizedPath = path.replace("\\", "/")
            val uri = if (normalizedPath.startsWith("/")) {
                "file://$normalizedPath"
            } else {
                "file:///$normalizedPath"
            }
            return ResourceID(uri)
        }

        fun fromPath(file: File): ResourceID = fromPath(file.path)

        fun fromPath(path: Path): ResourceID = fromPath(path.toFile())

        /**
         * Creates a [ResourceID] from the given URI.
         *
         * @param uri The URI.
         * @return The resource ID.
         */
        fun fromURI(uri: String): ResourceID {
            return ResourceID(uri)
        }
    }

}
