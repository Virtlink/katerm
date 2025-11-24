package net.pelsmaeker.katerm.generator.text

/**
 * The one-based line number in a document.
 */
@JvmInline
value class LineNumber(val value: Int): Comparable<LineNumber> {
    init {
        require(value > 0) { "Line number must be greater than or equal to 1, but was: $value" }
    }

    operator fun plus(increment: Int): LineNumber = LineNumber(value + increment)
    operator fun minus(decrement: Int): LineNumber = LineNumber(value - decrement)

    override fun compareTo(other: LineNumber): Int {
        return this.value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        /** The ONE line number. */
        val ONE = LineNumber(1)
    }
}
