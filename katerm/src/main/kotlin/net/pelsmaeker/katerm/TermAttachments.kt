package net.pelsmaeker.katerm

/**
 * Holds term attachments.
 */
interface TermAttachments {
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
        fun empty(): TermAttachments = EmptyTermAttachments
    }

    private object EmptyTermAttachments: TermAttachments {
        override fun isEmpty(): Boolean = true
        override fun <T> get(key: Class<T>): T? = null
    }
}