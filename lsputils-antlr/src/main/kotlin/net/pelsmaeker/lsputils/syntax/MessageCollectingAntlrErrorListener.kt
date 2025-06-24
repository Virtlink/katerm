package net.pelsmaeker.lsputils.syntax

import net.pelsmaeker.lsputils.diagnostics.Message
import net.pelsmaeker.lsputils.diagnostics.MessageCollector
import net.pelsmaeker.lsputils.diagnostics.MessageSeverity
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token

/**
 * Error listener that collects parse errors as messages.
 *
 * @property path The path of the source file being parsed.
 * @property collector The message collector to use.
 */
class MessageCollectingAntlrErrorListener(
    val path: String,
    val collector: MessageCollector,
) : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?,
    ) {
        // Create a message with the error details
        val message = Message(
            severity = MessageSeverity.ERROR,
            message = msg,
            path = path,
            span = (offendingSymbol as? Token)?.span,
        )
        collector.offer(message)
    }
}