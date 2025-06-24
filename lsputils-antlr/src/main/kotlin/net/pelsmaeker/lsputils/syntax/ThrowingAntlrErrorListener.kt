package net.pelsmaeker.lsputils.syntax

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.DefaultErrorStrategy
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException

/**
 * Error listener that throws an exception on parse error.
 *
 * Normally, a [RecognitionException] could be thrown, but this would be caught by the [DefaultErrorStrategy].
 * Instead, this error listener throws a [ParseCancellationException] to avoid the [DefaultErrorStrategy].
 */
object ThrowingAntlrErrorListener : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?,
    ) {
        throw ParseCancellationException("line $line:$charPositionInLine $msg", e)
    }
}