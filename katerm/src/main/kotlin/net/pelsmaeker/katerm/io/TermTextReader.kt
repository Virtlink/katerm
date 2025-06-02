package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.terms.Term
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass

/**
 * A term reader, reading a textual format.
 */
interface TermTextReader: TermReader {

    /**
     * Reads a term from a string.
     *
     * @param s the string to read from
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromString(s: String): Term? =
        read(StringReader(s))

    /**
     * Reads a term from a resource.
     *
     * @param cls the class whose classloader can find the resource
     * @param path the absolute path to the resource
     * @param charset the character set to use
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cls: KClass<*>, path: String, charset: Charset = Charsets.UTF_8): Term? =
        readFromResource(cls.java, path, charset)

    /**
     * Reads a term from a resource.
     *
     * @param cls the class whose classloader can find the resource
     * @param path the absolute path to the resource
     * @param charset the character set to use
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cls: Class<*>, path: String, charset: Charset = Charsets.UTF_8): Term? =
        readFromResource(cls.classLoader, path, charset)

    /**
     * Reads a term from a resource.
     *
     * @param cl the classloader that can find the resource
     * @param path the absolute path to the resource
     * @param charset the character set to use
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cl: ClassLoader, path: String, charset: Charset = Charsets.UTF_8): Term? {
        val stream = cl.getResourceAsStream(path.trimStart('/')) ?: throw ResourceNotFoundException(path)
        return read(stream, charset)
    }

    /**
     * Reads a term from a path.
     *
     * @param path the path to read from
     * @param charset the character set to use
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun read(path: Path, charset: Charset = Charsets.UTF_8): Term? =
        Files.newBufferedReader(path, charset).use { read(it) }

    /**
     * Reads a term from the specified input stream.
     *
     * @param stream the input stream to read from
     * @param charset the character set to use
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun read(stream: InputStream, charset: Charset): Term? =
        stream.bufferedReader(charset).use { read(it) }

    override fun read(stream: InputStream): Term? =
        read(stream, Charsets.UTF_8)

    /**
     * Reads a term from the specified reader.
     *
     * @param reader the reader to read from
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun read(reader: Reader): Term?
}