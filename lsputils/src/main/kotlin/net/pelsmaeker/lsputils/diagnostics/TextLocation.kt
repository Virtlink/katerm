package net.pelsmaeker.lsputils.diagnostics

/**
 * A location in text.
 *
 * @property offset The zero-based character offset of the location from the start of the text.
 * @property line The one-based line number of the location.
 * @property column The one-based character offset of the location from the start of the line.
 */
data class TextLocation(
    val offset: Int,
    val line: Int,
    val column: Int,
): Comparable<TextLocation> {

    init {
        require(offset >= 0) { "Offset must be non-negative: $offset" }
        require(line > 0) { "Line number must be greater than 0: $line" }
        require(column > 0) { "Column number must be greater than 0: $column" }
    }

    override fun compareTo(other: TextLocation): Int {
        val offsetComparison = offset.compareTo(other.offset)
        if (offsetComparison != 0) return offsetComparison

        val lineComparison = line.compareTo(other.line)
        if (lineComparison != 0) return lineComparison

        return column.compareTo(other.column)
    }

    override fun toString(): String = "$line:$column(@$offset)"
}