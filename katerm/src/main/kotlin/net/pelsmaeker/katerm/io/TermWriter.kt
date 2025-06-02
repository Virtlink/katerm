package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.terms.Term
import java.io.ByteArrayOutputStream
import java.io.OutputStream

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