package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.terms.Term
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass

/**
 * A term reader.
 */
interface TermReader {

    /**
     * Reads a term from a byte array.
     *
     * @param arr the byte array to read from
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromByteArray(arr: ByteArray): Term? =
        read(ByteArrayInputStream(arr))

    /**
     * Reads a term from a resource.
     *
     * @param cls the class whose classloader can find the resource
     * @param path the absolute path to the resource
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cls: KClass<*>, path: String): Term? =
        readFromResource(cls.java, path)

    /**
     * Reads a term from a resource.
     *
     * @param cls the class whose classloader can find the resource
     * @param path the absolute path to the resource
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cls: Class<*>, path: String): Term? =
        readFromResource(cls.classLoader, path)

    /**
     * Reads a term from a resource.
     *
     * @param cl the classloader that can find the resource
     * @param path the absolute path to the resource
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun readFromResource(cl: ClassLoader, path: String): Term? {
        val stream = cl.getResourceAsStream(path.trimStart('/')) ?: throw ResourceNotFoundException(path)
        return read(stream)
    }

    /**
     * Reads a term from a path.
     *
     * @param path the path to read from
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun read(path: Path): Term? =
        Files.newInputStream(path).use { read(it) }

    /**
     * Reads a term from the specified input stream.
     *
     * @param stream the input stream to read from
     * @return the read term; or `null` if there was no term to be read
     */
    @Throws(IOException::class)
    fun read(stream: InputStream): Term?
}
