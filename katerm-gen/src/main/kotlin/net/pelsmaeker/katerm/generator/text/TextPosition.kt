package net.pelsmaeker.katerm.generator.text

/**
 * A position in text.
 *
 * @property offset The zero-based character offset of the position from the start of the text.
 * @property line The one-based line number of the position.
 * @property char The zero-based character offset of the position from the start of the line.
 */
data class TextPosition(
    val offset: TextOffset,
    val line: LineNumber,
    val char: CharOffset,
): Comparable<TextPosition> {

    override fun compareTo(other: TextPosition): Int {
        val offsetComparison = offset.compareTo(other.offset)
        if (offsetComparison != 0) return offsetComparison

        val lineComparison = line.compareTo(other.line)
        if (lineComparison != 0) return lineComparison

        return char.compareTo(other.char)
    }

    override fun toString(): String = "$line:$char(@$offset)"

    companion object {
        /** An empty position at the start of the text. */
        val ZERO = TextPosition(TextOffset.ZERO, LineNumber.ONE, CharOffset.ZERO)

        operator fun invoke(offset: Int, line: Int, column: Int) =
            TextPosition(TextOffset(offset), LineNumber(line), CharOffset(column))
    }
}
