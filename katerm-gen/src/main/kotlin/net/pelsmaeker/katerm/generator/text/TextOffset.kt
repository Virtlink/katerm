package net.pelsmaeker.katerm.generator.text

/**
 * The zero-based text offset in a document.
 */
@JvmInline
value class TextOffset(val value: Int): Comparable<TextOffset> {
    init {
        require(value >= 0) { "Text offset must be greater than or equal to 0, but was: $value" }
    }

    operator fun plus(increment: Int): TextOffset = TextOffset(value + increment)
    operator fun plus(increment: CharOffset): TextOffset = TextOffset(value + increment.value)
    operator fun minus(decrement: Int): TextOffset = TextOffset(value - decrement)
    operator fun minus(decrement: TextOffset): CharOffset = CharOffset(value - decrement.value)

    override fun compareTo(other: TextOffset): Int {
        return this.value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /** The zero text offset. */
        val ZERO = TextOffset(0)
    }
}
