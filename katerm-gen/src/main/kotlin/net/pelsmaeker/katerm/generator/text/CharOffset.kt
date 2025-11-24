package net.pelsmaeker.katerm.generator.text

/**
 * The one-based character offset in a document.
 */
@JvmInline
value class CharOffset(val value: Int): Comparable<CharOffset> {
    init {
        require(value >= 0) { "Character offset must be greater than or equal to 0, but was: $value" }
    }

    operator fun plus(increment: Int): CharOffset = CharOffset(value + increment)
    operator fun minus(decrement: Int): CharOffset = CharOffset(value - decrement)

    override fun compareTo(other: CharOffset): Int {
        return this.value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /** The zero character offset. */
        val ZERO = CharOffset(0)
    }
}
