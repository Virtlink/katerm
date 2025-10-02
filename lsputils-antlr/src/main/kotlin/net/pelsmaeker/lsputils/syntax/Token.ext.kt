package net.pelsmaeker.lsputils.syntax

import net.pelsmaeker.lsputils.diagnostics.TextSpan
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

/**
 * Gets the span of the specified token.
 *
 * @receiver The token to get the span for.
 * @return The span of the token, or `null` if the token has no span.
 */
val Token.span: TextSpan? get() {
    val startLine = this.line
    val startCharacter = this.charPositionInLine + 1
    val startOffset = this.startIndex
    if (startOffset == -1) return null
    val length = this.text.length
    val endLine = startLine
    val endCharacter = startCharacter + length
    val endOffset = this.stopIndex + 1
    return TextSpan(
        startOffset, startLine, startCharacter,
        endOffset, endLine, endCharacter,
    )
}

/**
 * Gets the span of the specified parser rule context.
 *
 * @receiver The parser rule context to get the span for.
 * @return The span of the parser rule context, or `null` if the rule context has no span.
 */
val ParserRuleContext.span: TextSpan? get() {
    val startSpan = this.start?.span
    val endSpan = this.stop?.span
    if (startSpan == null || endSpan == null) return null
    return TextSpan(startSpan.start, endSpan.end)
}
