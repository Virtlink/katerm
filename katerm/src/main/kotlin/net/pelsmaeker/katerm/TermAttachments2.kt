package net.pelsmaeker.katerm

/**
 * Holds term attachments.
 */
class TermAttachments2 private constructor(
    /** A map from keys to values, where the T parameter of the key is the type of the value. */
    private val attachments: Map<Key<*>, Any>,
) {

    /** A term attachment key. */
    interface Key<out T> {
        /** The type of the value associated with this key. */
        val type: Class<out T>
    }

    /**
     * Gets the attachment with the specified key.
     *
     * @param key the key of the attachment
     * @return the attachment associated with the specified key, if found; otherwise, `null`
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
     * @param pair the key/value pair to insert
     * @return a new map with the specified attachment added
     */
    fun add(pair: Pair<Key<Any>, Any>): TermAttachments2 {
        require(pair.first.type.isInstance(pair.second)) {
            "The value ${pair.second} (${pair.second::class.java}) is not an instance of ${pair.first.type}"
        }
        return TermAttachments2(attachments + (pair.first to pair.second))
    }

    /**
     * Inserts attachments with the specified key/value pairs into this map.
     *
     * If a key is already present, it is replaced.
     *
     * @param pairs the key/value pairs to insert
     * @return a new map with the specified attachments added
     */
    fun addAll(vararg pairs: Pair<Key<*>, Any>): TermAttachments2 =
        addAll(pairs.asIterable())

    /**
     * Inserts attachments with the specified key/value pairs into this map.
     *
     * If a key is already present, it is replaced.
     *
     * @param pairs the key/value pairs to insert
     * @return a new map with the specified attachments added
     */
    fun addAll(pairs: Iterable<Pair<Key<*>, Any>>): TermAttachments2 {
        pairs.forEach { pair ->
            require(pair.first.type.isInstance(pair.second)) {
                "The value ${pair.second} (${pair.second::class.java}) is not an instance of ${pair.first.type}"
            }
        }
        return TermAttachments2(attachments + pairs.map { it.first to it.second })
    }

    /**
     * Removes the attachment with the specified key from this map.
     *
     * If the key is not present, nothing happens.
     *
     * @param key the key of the attachment to remove
     * @return a new map with the specified attachment removed
     */
    fun remove(key: Key<*>): TermAttachments2 {
        return TermAttachments2(attachments - key)
    }

    /**
     * Inserts an attachment with the specified key/value pair into this map.
     *
     * If the key is already present, it is replaced.
     *
     * @param pair the key/value pair to insert
     * @return a new map with the specified attachment added
     */
    operator fun <T> plus(pair: Pair<Key<T>, T>): TermAttachments2 = add(pair as Pair<Key<Any>, Any>)

    /**
     * Removes the attachment with the specified key from this map.
     *
     * If the key is not present, nothing happens.
     *
     * @param key the key of the attachment to remove
     * @return a new map with the specified attachment removed
     */
    operator fun minus(key: Key<*>): TermAttachments2 = remove(key)

    companion object {
        /** An empty instance that can be reused. */
        private val empty = TermAttachments2(emptyMap())

        /** Gets an empty term attachments object. */
        fun empty(): TermAttachments2 = empty

        /** Gets an empty term attachments object. */
        fun of(): TermAttachments2 = empty

        /** Gets a term attachments object with the specified attachment. */
        fun <T> of(attachment: Pair<Key<T>, T>): TermAttachments2 = from(listOf(attachment.first to listOf(attachment.second)))

        /** Gets a term attachments object with the specified attachments. */
        fun of(vararg attachments: Pair<Key<*>, Any>): TermAttachments2 = from(attachments.asList())

        fun from(attachments: Map<Key<*>, Any>) = from(attachments.entries.map { it.key to it.value })

        /** Gets a term attachments object from the specified iterable of attachments. */
        fun from(attachments: Collection<Pair<Key<*>, Any>>): TermAttachments2 {
            return empty.addAll(attachments)
        }
    }

}