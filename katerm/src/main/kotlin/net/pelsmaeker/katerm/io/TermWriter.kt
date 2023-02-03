package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.Term
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.StringWriter
import java.io.Writer
import java.nio.charset.Charset

/**
 * Writes a term.
 */
fun interface TermWriter {

    /**
     * Writes the specified term to the specified output stream.
     *
     * @param term the term to write
     * @param output the output stream to print to
     */
    fun write(term: Term, output: OutputStream)

    /**
     * Writes the specified term as a byte array.
     *
     * @param term the term to write
     * @return the resulting byte array
     */
    fun writeToByteArray(term: Term): ByteArray {
        return ByteArrayOutputStream().use { output ->
            write(term, output)
            output.toByteArray()
        }
    }

}