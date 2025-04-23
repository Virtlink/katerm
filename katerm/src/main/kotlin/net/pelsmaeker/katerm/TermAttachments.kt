package net.pelsmaeker.katerm

/**
 * Holds term attachments.
 *
 * @property attachments A map from keys to values, where the T parameter of the key is the type of the value.
 */
class TermAttachments private constructor(
    private val attachments: Map<Key<*>, Any>,
) {

    /**
     * A term attachment key.
     *
     * @property type The type of the value associated with this key.
     */
    abstract class Key<out T>(
        val type: Class<out T>,
    )

    /**
     * Gets the attachment with the specified key.
     *
     * @param key The key of the attachment.
     * @return The attachment associated with the specified key, if found; otherwise, `null`.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: Key<T>): T? = attachments[key] as? T

    /**
     * Determines whether an attachment with the specified key is present.
     *
     * @param key the key of the attachment
     * @return `true` if an attachment with the specified key is present; otherwise, `false`
     */
    fun containsKey(key: Key<*>): Boolean = attachments.containsKey(key)

    /**
     * Inserts an attachment with the specified key/value pair into this map.
     *
     * If the key is already present, it is replaced.
     *
     * @param pair The key/value pair to insert.
     * @return A new map with the specified attachment added.
     */
    fun add(pair: Pair<Key<Any>, Any>): TermAttachments {
        require(pair.first.type.isInstance(pair.second)) {
            "The value ${pair.second} (${pair.second::class.java}) is not an instance of ${pair.first.type}"
        }
        return TermAttachments(attachments + (pair.first to pair.second))
    }

    /**
     * Inserts attachments with the specified key/value pairs into this map.
     *
     * If a key is already present, it is replaced.
     *
     * @param pairs The key/value pairs to insert.
     * @return A new map with the specified attachments added.
     */
    fun addAll(vararg pairs: Pair<Key<*>, Any>): TermAttachments =
        addAll(pairs.asIterable())

    /**
     * Inserts attachments with the specified key/value pairs into this map.
     *
     * If a key is already present, it is replaced.
     *
     * @param pairs The key/value pairs to insert.
     * @return A new map with the specified attachments added.
     */
    fun addAll(pairs: Iterable<Pair<Key<*>, Any>>): TermAttachments {
        pairs.forEach { pair ->
            require(pair.first.type.isInstance(pair.second)) {
                "The value ${pair.second} (${pair.second::class.java}) is not an instance of ${pair.first.type}"
            }
        }
        return TermAttachments(attachments + pairs.map { it.first to it.second })
    }

    /**
     * Removes the attachment with the specified key from this map.
     *
     * If the key is not present, nothing happens.
     *
     * @param key The key of the attachment to remove.
     * @return A new map with the specified attachment removed.
     */
    fun remove(key: Key<*>): TermAttachments {
        return TermAttachments(attachments - key)
    }

    /**
     * Inserts an attachment with the specified key/value pair into this map.
     *
     * If the key is already present, it is replaced.
     *
     * @param pair The key/value pair to insert.
     * @return A new map with the specified attachment added.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> plus(pair: Pair<Key<T>, T>): TermAttachments = add(pair as Pair<Key<Any>, Any>)

    /**
     * Removes the attachment with the specified key from this map.
     *
     * If the key is not present, nothing happens.
     *
     * @param key The key of the attachment to remove.
     * @return A new map with the specified attachment removed.
     */
    operator fun minus(key: Key<*>): TermAttachments = remove(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TermAttachments) return false
        val that = other as TermAttachments
        return this.attachments == that.attachments
    }

    override fun hashCode(): Int {
        return attachments.hashCode()
    }

    override fun toString(): String {
        return attachments.toString()
    }

    companion object {
        /** An empty instance that can be reused. */
        private val empty = TermAttachments(emptyMap())

        /** Gets an empty term attachments object. */
        fun empty(): TermAttachments = empty

        /** Gets an empty term attachments object. */
        fun of(): TermAttachments = empty

        /**
         * Gets a term attachments object with the specified attachment.
         *
         * @param attachment The attachment to look for.
         */
        fun <T> of(attachment: Pair<Key<T>, T>): TermAttachments = from(listOf(attachment.first to listOf(attachment.second)))

        /**
         * Gets a term attachments object with the specified attachments.
         *
         * @param attachments The attachments to look for.
         */
        fun of(vararg attachments: Pair<Key<*>, Any>): TermAttachments = from(attachments.asList())

        /**
         * Gets a term attachments object from the specified iterable of attachments.
         *
         * @param attachments The attachments to look for.
         */
        fun from(attachments: Map<Key<*>, Any>) = from(attachments.entries.map { it.key to it.value })

        /**
         * Gets a term attachments object from the specified iterable of attachments.
         *
         * @param attachments The attachments to look for.
         */
        fun from(attachments: Collection<Pair<Key<*>, Any>>): TermAttachments {
            return empty.addAll(attachments)
        }
    }

}