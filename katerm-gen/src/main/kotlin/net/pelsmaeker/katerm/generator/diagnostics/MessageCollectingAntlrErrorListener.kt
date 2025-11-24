package net.pelsmaeker.katerm.generator.diagnostics

import net.pelsmaeker.katerm.generator.ResourceID
import net.pelsmaeker.katerm.generator.text.TextOrigin
import net.pelsmaeker.katerm.generator.text.computeRange
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.ParseCancellationException

/**
 * Error listener that collects parse errors as messages.
 *
 * @property resource The resource ID of the source file being parsed.
 * @property collector The message collector to use.
 */
class MessageCollectingAntlrErrorListener(
    val resource: ResourceID,
    val collector: MessageCollector,
    val sourceName: String = "Parser",
) : BaseErrorListener() {

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?,
    ) {
        // Create a message with the error details
        val message = Message(
            severity = MessageSeverity.ERROR,
            text = msg,
            origin = TextOrigin(resource, (offendingSymbol as? Token)?.computeRange()),
            sourceName = sourceName,
        )
        val cont = collector.offer(message)
        if (!cont) throw ParseCancellationException("line $line:$charPositionInLine $msg")
    }
}
