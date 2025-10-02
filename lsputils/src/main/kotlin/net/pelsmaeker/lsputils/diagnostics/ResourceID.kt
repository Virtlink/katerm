package net.pelsmaeker.lsputils.diagnostics

@JvmInline
value class ResourceID private constructor(val uri: String) {
    companion object {
        fun fromPath(path: String): ResourceID {
            val normalizedPath = path.replace("\\", "/")
            val uri = if (normalizedPath.startsWith("/")) {
                "file://$normalizedPath"
            } else {
                "file:///$normalizedPath"
            }
            return ResourceID(uri)
        }

        fun fromURI(uri: String): ResourceID {
            return ResourceID(uri)
        }
    }
}