package net.pelsmaeker.katerm.generator.text


/**
 * A range in text.
 *
 * @property startOffset The zero-based character offset of the start location from the start of the text, inclusive.
 * @property startLine The one-based line number of the start location, inclusive.
 * @property startColumn The one-based character offset of the start location from the start of the line, inclusive.
 * @property endOffset The zero-based character offset of the end location from the start of the text, exclusive.
 * @property endLine The one-based line number of the end location, exclusive.
 * @property endColumn The one-based character offset of the end location from the start of the line, exclusive.
 */
data class TextRange(
    val startOffset: TextOffset,
    val startLine: LineNumber,
    val startColumn: CharOffset,
    val endOffset: TextOffset,
    val endLine: LineNumber,
    val endColumn: CharOffset,
) {

    init {
        require(endOffset >= startOffset) { "End offset must be greater than or equal to start offset." }
        require(endLine >= startLine) { "End line number must be greater than or equal to start line number." }
        require(endColumn >= startColumn) { "End column number must be greater than or equal to start column number." }
    }

    constructor(
        start: TextPosition,
        end: TextPosition,
    ) : this(
        start.offset,
        start.line,
        start.char,
        end.offset,
        end.line,
        end.char,
    )

    /** The length of the range in characters. */
    val length: Int get() = endOffset.value - startOffset.value

    /** Determines whether the range is empty. */
    fun isEmpty(): Boolean = startOffset == endOffset
    /** Determines whether the range is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /** The start location of the range. */
    val start: TextPosition get() = TextPosition(startOffset, startLine, startColumn)
    /** The end location of the range. */
    val end: TextPosition get() = TextPosition(endOffset, endLine, endColumn)

    /**
     * Determines whether the given location is within the range.
     *
     * @param location The location to check.
     * @return `true` if the location is within the range; otherwise, `false`.
     */
    operator fun contains(location: TextPosition): Boolean {
        return location.offset.value in startOffset.value until endOffset.value
    }

    /**
     * Determines whether the given range is entirely within this range.
     *
     * @param range The range to check.
     * @return `true` if the given range is entirely within this range; otherwise, `false`.
     */
    operator fun contains(range: TextRange): Boolean {
        return this.startOffset <= range.startOffset && this.endOffset >= range.endOffset
    }

    /**
     * Determines whether the given location is within or at the boundaries of the range.
     *
     * @param location The location to check.
     * @return `true` if the location is within or at the boundaries of the range; otherwise, `false`.
     */
    fun containsOrAt(location: TextPosition): Boolean {
        return location.offset in startOffset..endOffset
    }

    /**
     * Determines whether this range overlaps with another range.
     *
     * Two ranges overlap if they share at least one character.
     * For example, the ranges [0, 5) and [5, 10) do not overlap, but the ranges [0, 5) and [4, 10) do overlap.
     *
     * @param other The other range to check for overlap.
     * @return `true` if the ranges overlap; otherwise, `false`.
     */
    fun overlaps(other: TextRange): Boolean {
        return this.startOffset < other.endOffset && other.startOffset < this.endOffset
    }

    override fun toString(): String = "$start-$end"

    companion object {
        /** An empty range at the start of the text. */
        val ZERO = TextRange(TextPosition.Companion.ZERO, TextPosition.Companion.ZERO)
    }
}
