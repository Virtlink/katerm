package net.pelsmaeker.katerm.generator

import net.pelsmaeker.katerm.generator.ast.KatermAstBuilder
import net.pelsmaeker.katerm.generator.ast.FileUnit
import net.pelsmaeker.lsputils.diagnostics.FailFastMessageCollectorWrapper
import net.pelsmaeker.lsputils.diagnostics.MessageCollector
import net.pelsmaeker.lsputils.diagnostics.ResourceID
import net.pelsmaeker.lsputils.syntax.MessageCollectingAntlrErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader

/**
 * Parser for the Katerm language.
 *
 * @property failFast If true, the parser stop parsing and return an error immediately when it encounters an error.
 */
class KatermParser(
    val failFast: Boolean = false,
) {

    /**
     * Parses the specified string into an AST.
     *
     * @param resource The resource ID of the file being parsed.
     * @param str The string to parse.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(resource: ResourceID, str: String, messageCollector: MessageCollector): FileUnit?
            = StringReader(str).use { parse(resource, it, messageCollector) }

    /**
     * Parses the content from the specified input stream into an AST.
     *
     * @param resource The resource ID of the file being parsed.
     * @param stream The input stream to read from.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(resource: ResourceID, stream: InputStream, messageCollector: MessageCollector): FileUnit?
            = InputStreamReader(stream).use { parse(resource, it, messageCollector) }

    /**
     * Parses the given text and returns the AST.
     *
     * @param resource The resource ID of the file being parsed.
     * @param reader The reader to read from.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(resource: ResourceID, reader: Reader, messageCollector: MessageCollector): FileUnit? {
        val charStream = CharStreams.fromReader(reader)

        val errorListener = MessageCollectingAntlrErrorListener(
            resource = resource,
            collector = if (failFast) FailFastMessageCollectorWrapper(messageCollector) else messageCollector,
            sourceName = "Katerm Parser",
        )

        val lexer = KatermAntlrLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val tokens = CommonTokenStream(lexer)

        val parser = KatermAntlrParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val unit = parser.unit()

        return KatermAstBuilder().build(unit)
    }
}