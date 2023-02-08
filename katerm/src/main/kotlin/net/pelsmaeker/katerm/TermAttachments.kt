package net.pelsmaeker.katerm

/**
 * Holds term attachments.
 */
sealed interface TermAttachments {
    /** Whether the set of attachments is empty. */
    fun isEmpty(): Boolean
    /** Whether the set of attachments is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /**
     * Gets the attachment with the specified key.
     *
     * @param key the key, the class of the attachment
     * @return the attachment, if found; otherwise, `null`
     */
    operator fun <T> get(key: Class<T>): T?

    companion object {
        /** Gets an empty term attachments object. */
        fun empty(): TermAttachments = EmptyTermAttachments

        /** Gets an empty term attachments object. */
        fun of(): TermAttachments = EmptyTermAttachments

        /** Gets a term attachments object with the specified attachment. */
        fun of(attachment: Any): TermAttachments = SingletonTermAttachment(attachment)

        /** Gets a term attachments object with the specified attachments. */
        fun of(vararg attachments: Any): TermAttachments = from(attachments.asList())

        /** Gets a term attachments object from the specified iterable of attachments. */
        fun from(attachments: List<Any>): TermAttachments {
            val distinctAttachments = attachments.distinctBy { it::class.java } // Also creates a safety copy
            require(attachments.size == distinctAttachments.size) { "Two or more attachments have the same class." }
            return when (distinctAttachments.size) {
                0 -> EmptyTermAttachments
                1 -> SingletonTermAttachment(distinctAttachments[0])
                else -> MultiTermAttachment(distinctAttachments);
            }
        }
    }

    // This is a `private object` so that there is always just one instance.
    // This is important because it is the default value for `Term.attachments`.

    /** An empty term attachments object. */
    private object EmptyTermAttachments: TermAttachments {
        override fun isEmpty(): Boolean = true
        override fun <T> get(key: Class<T>): T? = null
    }

    /** A singleton term attachments object. */
    private data class SingletonTermAttachment(
        private val attachment: Any
    ): TermAttachments {
        override fun isEmpty(): Boolean = false

        override fun <T> get(key: Class<T>): T? =
            attachment.takeIf { key::class.java == it }?.let { return it as T }

    }

    /** A multiple attachments object. */
    private class MultiTermAttachment(
        private val attachments: List<Any>
    ): TermAttachments {
        override fun isEmpty(): Boolean = attachments.isEmpty()

        override fun <T> get(key: Class<T>): T? =
            attachments.firstOrNull { key::class.java == it }?.let { return it as T }
    }
}