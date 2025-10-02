package net.pelsmaeker.lsputils.diagnostics

/**
 * A span in text.
 *
 * @property startOffset The zero-based character offset of the start location from the start of the text, inclusive.
 * @property startLine The one-based line number of the start location, inclusive.
 * @property startColumn The one-based character offset of the start location from the start of the line, inclusive.
 * @property endOffset The zero-based character offset of the end location from the start of the text, exclusive.
 * @property endLine The one-based line number of the end location, exclusive.
 * @property endColumn The one-based character offset of the end location from the start of the line, exclusive.
 */
data class TextSpan(
    val startOffset: Int,
    val startLine: Int,
    val startColumn: Int,
    val endOffset: Int,
    val endLine: Int,
    val endColumn: Int,
) {

    init {
        require(startLine > 0) { "Start line number must be greater than 0." }
        require(startColumn > 0) { "Start column number must be greater than 0." }
        require(endLine > 0) { "End line number must be greater than 0." }
        require(endColumn > 0) { "End column number must be greater than 0." }
        require(endOffset >= startOffset) { "End offset must be greater than or equal to start offset." }
        require(endLine >= startLine) { "End line number must be greater than or equal to start line number." }
        require(endColumn >= startColumn) { "End column number must be greater than or equal to start column number." }
    }

    constructor(
        start: TextLocation,
        end: TextLocation,
    ) : this(
        start.offset,
        start.line,
        start.column,
        end.offset,
        end.line,
        end.column,
    )

    /** The length of the span in characters. */
    val length: Int get() = endOffset - startOffset

    /** Determines whether the span is empty. */
    fun isEmpty(): Boolean = startOffset == endOffset
    /** Determines whether the span is not empty. */
    fun isNotEmpty(): Boolean = !isEmpty()

    /** The start location of the span. */
    val start: TextLocation get() = TextLocation(startOffset, startLine, startColumn)
    /** The end location of the span. */
    val end: TextLocation get() = TextLocation(endOffset, endLine, endColumn)

    override fun toString(): String = "$start-$end"

    companion object {
        /** An empty span at the start of the text. */
        val ZERO = TextSpan(TextLocation.ZERO, TextLocation.ZERO)
    }
}