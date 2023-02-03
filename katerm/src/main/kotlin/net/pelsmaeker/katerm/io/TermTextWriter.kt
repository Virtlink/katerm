package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.Term
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.StringWriter
import java.io.Writer
import java.nio.charset.Charset

/**
 * Writes a term as text.
 */
fun interface TermTextWriter: TermWriter {

    /**
     * Writes the specified term to the specified writer.
     *
     * @param term the term to write
     * @param writer the writer to print to
     */
    fun write(term: Term, writer: Writer)

    override fun write(term: Term, output: OutputStream) = write(term, output, Charsets.UTF_8)

    /**
     * Writes the specified term to the specified output stream
     * using the specified character set.
     *
     * @param term the term to write
     * @param output the output stream to print to
     * @param charset the character set to use
     */
    fun write(term: Term, output: OutputStream, charset: Charset) {
        output.writer(charset).use { writer ->
            write(term, writer)
        }
    }

    /**
     * Writes the specified term to a string.
     *
     * @param term the term to write
     * @return the resulting string
     */
    fun writeToString(term: Term): String {
         return StringWriter().also { writer ->
            write(term, writer)
        }.toString()
    }


}