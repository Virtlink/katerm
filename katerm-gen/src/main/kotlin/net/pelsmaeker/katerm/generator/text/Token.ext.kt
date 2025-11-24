package net.pelsmaeker.katerm.generator.text

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

/**
 * Gets the span of the specified token.
 *
 * @receiver The token to get the span for.
 * @return The span of the token, or `null` if the token has no span.
 */
fun Token.computeRange(): TextRange? {
    val startLine = this.line
    val startCharacter = this.charPositionInLine
    val startOffset = this.startIndex
    if (startOffset == -1) return null
    val length = this.text.length
    val endLine = startLine
    val endCharacter = startCharacter + length
    val endOffset = this.stopIndex + 1
    return TextRange(
        TextPosition(startOffset, startLine, startCharacter),
        TextPosition(endOffset, endLine, endCharacter),
    )
}

/**
 * Gets the span of the specified parser rule context.
 *
 * @receiver The parser rule context to get the span for.
 * @return The span of the parser rule context, or `null` if the rule context has no span.
 */
fun ParserRuleContext.computeRange(): TextRange? {
    val startSpan = this.start?.computeRange()
    val endSpan = this.stop?.computeRange()
    if (startSpan == null || endSpan == null) return null
    return TextRange(startSpan.start, endSpan.end)
}
