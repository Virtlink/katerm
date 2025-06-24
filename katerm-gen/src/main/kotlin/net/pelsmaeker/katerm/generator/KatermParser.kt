package net.pelsmaeker.katerm.generator

import net.pelsmaeker.katerm.generator.ast.KatermAstBuilder
import net.pelsmaeker.katerm.generator.ast.KatermUnit
import net.pelsmaeker.lsputils.diagnostics.FailFastMessageCollectorWrapper
import net.pelsmaeker.lsputils.diagnostics.MessageCollector
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
     * @param path The path of the file being parsed.
     * @param str The string to parse.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(path: String, str: String, messageCollector: MessageCollector): KatermUnit?
            = StringReader(str).use { parse(path, it, messageCollector) }

    /**
     * Parses the content from the specified input stream into an AST.
     *
     * @param path The path of the file being parsed.
     * @param stream The input stream to read from.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(path: String, stream: InputStream, messageCollector: MessageCollector): KatermUnit?
            = InputStreamReader(stream).use { parse(path, it, messageCollector) }

    /**
     * Parses the given text and returns the AST.
     *
     * @param path The path of the file being parsed.
     * @param reader The reader to read from.
     * @param messageCollector The message collector to use.
     * @return The AST if the input was successfully parsed or recovered; otherwise, `null`.
     */
    fun parse(path: String, reader: Reader, messageCollector: MessageCollector): KatermUnit? {
        val charStream = CharStreams.fromReader(reader)

        val errorListener = MessageCollectingAntlrErrorListener(
            path = path,
            collector = if (failFast) FailFastMessageCollectorWrapper(messageCollector) else messageCollector
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