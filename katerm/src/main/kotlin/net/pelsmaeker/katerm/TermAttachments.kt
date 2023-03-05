package net.pelsmaeker.katerm

/**
 * Holds term attachments.
 */
sealed interface TermAttachments {
    // TODO: Make this a multiset Class -> Value,
    //  and perhaps even add a way to specify the class under which the value is registered,
    //  or a custom key is even better

    /** The number of attachments (key/value-pairs) in this map. */
    val size: Int
    /** Whether the set of attachments is empty. */
    fun isEmpty(): Boolean = size == 0
    /** Whether the set of attachments is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /** Whether the map of attachments contains any values associated with the specified key. */
    fun containsKey(key: Any): Boolean = get(key).isNotEmpty()

    /**
     * Gets the attachments with the specified key.
     *
     * @param key the key of the attachment
     * @return the collection of attachments associated with the specified key, if found;
     * otherwise, an empty collection
     */
    operator fun get(key: Any): Collection<Any> = getOrDefault(key, emptyList())

    /**
     * Gets the attachments with the specified key.
     *
     * @param key the key of the attachment
     * @param default the default value to return if no values are associated with the specified key
     * @return the collection of attachments associated with the specified key, if found;
     * otherwise, the given default value
     */
    fun getOrDefault(key: Any, default: Collection<Any>): Collection<Any>

    /** The set of keys of the attachments. */
    val keys: Set<Any>

    /** The collection of key/value pairs. */
    val entries: Collection<Map.Entry<Any, Any>>


//    infix operator fun plus(other: TermAttachments): TermAttachments = when {
//        isEmpty() -> other
//        other.isEmpty() -> this
//        else -> from(this.toSet() + other.toSet())
//    }
//
//    infix operator fun plus(other: Any): TermAttachments = when {
//        isEmpty() -> of(other)
//        else -> from(this.toSet() + other)
//    }

    companion object {
        /** Gets an empty term attachments object. */
        fun empty(): TermAttachments = EmptyTermAttachments

        /** Gets an empty term attachments object. */
        fun of(): TermAttachments = EmptyTermAttachments

        /** Gets a term attachments object with the specified attachment. */
        fun of(attachment: Pair<Any, Any>): TermAttachments = SingletonTermAttachments(attachment.first, listOf(attachment.second))

        /** Gets a term attachments object with the specified attachments. */
        fun of(vararg attachments: Pair<Any, Any>): TermAttachments = from(attachments.asList())

        fun from(attachments: Map<Any, Collection<Any>>) = from(attachments.entries.map { it.key to it.value })

        /** Gets a term attachments object from the specified iterable of attachments. */
        fun from(attachments: Collection<Pair<Any, Any>>): TermAttachments {
            return when (attachments.size) {
                0 -> EmptyTermAttachments
                1 -> attachments.first().let { SingletonTermAttachments(it.first, listOf(it.second)) }
                else -> MultiTermAttachments(attachments.groupBy { it.first }.mapValues { it.value.map { (_, v) -> v } });
            }
        }
    }

    // This is a `private object` so that there is always just one instance.
    // This is important because it is the default value for [Term.termAttachments]

    /** An empty term attachments object. */
    private object EmptyTermAttachments: TermAttachments {
        override val size: Int get() = 0

        override fun getOrDefault(key: Any, default: Collection<Any>): Collection<Any> {
            return emptyList()
        }

        override val keys: Set<Any> get() = emptySet()
        override val entries: Set<Map.Entry<Any, Any>> get() = emptySet()
    }

    /** A singleton term attachments object. */
    private data class SingletonTermAttachments(
        private val key: Any,
        private val values: Collection<Any>,
    ): TermAttachments {
        override val size: Int get() = 1

        override fun getOrDefault(key: Any, default: Collection<Any>): Collection<Any> {
            return if (key == this.key) values else default
        }

        override val keys: Set<Any> get() = setOf(key)
        override val entries: Collection<Map.Entry<Any, Any>>
            get() = values.map { Entry(key, it) }   // TODO: Make this a view or something without all the copying

    }

    /** A multiple attachments object. */
    private class MultiTermAttachments(
        private val map: Map<Any, Collection<Any>>
    ): TermAttachments {
        override val size: Int = map.values.fold(0) { acc, values -> acc + values.size }

        override fun getOrDefault(key: Any, default: Collection<Any>): Collection<Any> {
            return map[key] ?: default
        }

        override val keys: Set<Any>
            get() = map.keys
        override val entries: Collection<Map.Entry<Any, Any>>
            get() = map.entries.flatMap { (key, values) -> values.map { Entry(key, it) } }   // TODO: Make this a view or something without all the copying
    }

    private data class Entry(
        override val key: Any,
        override val value: Any,
    ): Map.Entry<Any, Any>
}