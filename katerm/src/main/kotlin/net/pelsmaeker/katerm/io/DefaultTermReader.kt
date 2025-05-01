package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.*
import net.pelsmaeker.katerm.TermBuilder
import net.pelsmaeker.katerm.annotations.TermAnnotationKey
import net.pelsmaeker.katerm.attachments.TermAttachments
import java.io.*

/**
 * An ATerm reader.
 */
class DefaultTermReader(
    private val termFactory: SimpleTermBuilder
): TermReader, TermTextReader {

    override fun read(reader: Reader): Term? {
        return readTerm(PushbackReader(reader, 1))
    }

    /**
     * Reads a term from a reader.
     *
     * @param reader the reader to read from
     * @return the read term; or `null` if there was no term to be read
     */
    private fun readTerm(reader: PushbackReader): Term? {
        if (!readWhitespace(reader)) return null

        val ch: Char = reader.peekOrThrow()

        return when {
            ch == '[' -> readList(reader)
            ch == '(' -> readTuple(reader)
            ch == '"' -> readString(reader)
            ch == '<' -> readPlaceholder(reader)
            ch == '.' -> readNumber(reader)
            ch.isDigit() -> readNumber(reader)
            ch.isLetter() -> readAppl(reader)
            else -> throw FormatException("Invalid term starting with $ch.")
        }
    }

    /**
     * Reads a list term.
     *
     * The reader must be positioned at the opening `[` character
     * of the list, and will be positioned at the character following the closing `]`
     * character of the list or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readList(reader: PushbackReader): ListTerm<Term> = reader.run {
        readExpected("list", '[')

        val terms = readTermSequence(reader, ',', ']')
        val attachments = readAttachments(reader)

        return termFactory.newList(terms, attachments)
    }

    /**
     * Reads a tuple.
     *
     * The reader must be positioned at the opening `(` character
     * of the tuple, and will be positioned at the character following the closing `)`
     * character of the tuple or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readTuple(reader: PushbackReader): ApplTerm {
        return readAppl(reader)
    }

    /**
     * Reads a string.
     *
     * The reader must be positioned at the opening `"` character
     * of the string, and will be positioned at the character following the closing `"`
     * character of the string or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readString(reader: PushbackReader): StringTerm = reader.run {
        readExpected("string", '"')

        val sb = StringBuilder()
        var ch = readOrThrow()

        while (ch != '"') {
            if (ch == '\\')
            {
                ch = readOrThrow()
                when (ch) {
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    'f' -> sb.append('\u000C')
                    't' -> sb.append('\t')
                    '\\' -> sb.append('\\')
                    '\'' -> sb.append('\'')
                    '"' -> sb.append('"')
                    'u' -> sb.append(readEscapedHexUnicode(reader, 4, 4))
                    'U' -> sb.append(readEscapedHexUnicode(reader, 8, 8))
                    'x' -> sb.append(readEscapedHexUnicode(reader, 1, 4))
                    else -> throw FormatException("Unrecognized escape sequence: '\\$ch'")
                }
            } else {
                sb.append(ch)
            }

            ch = readOrThrow()
        }

        val attachments = readAttachments(reader)
        return termFactory.newString(sb.toString(), attachments)
    }

    /**
     * Reads a sequence of decimal digits into a Unicode character.
     *
     * @param reader the reader to read from
     * @param minLength the minimum length of the digits to read
     * @param maxLength the maximum length of the digits to read
     */
    private fun readEscapedDecUnicode(reader: PushbackReader, minLength: Int, maxLength: Int): Char {
        return readEscapedUnicode(reader, { it.isDecDigit() }, 10, minLength, maxLength)
    }

    /**
     * Reads a sequence of hexadecimal digits into a Unicode character.
     *
     * @param reader the reader to read from
     * @param minLength the minimum length of the digits to read
     * @param maxLength the maximum length of the digits to read
     */
    private fun readEscapedHexUnicode(reader: PushbackReader, minLength: Int, maxLength: Int): Char {
        return readEscapedUnicode(reader, { it.isHexDigit() }, 16, minLength, maxLength)
    }

    /**
     * Reads a sequence of digits into a Unicode character.
     *
     * @param reader the reader to read from
     * @param validator the character validator function
     * @param radix the radix of the number to read
     * @param minLength the minimum length of the digits to read
     * @param maxLength the maximum length of the digits to read
     */
    private fun readEscapedUnicode(reader: PushbackReader, validator: (Char) -> Boolean, radix: Int, minLength: Int, maxLength: Int): Char {
        val sb = StringBuilder()
        for (i in 0 until maxLength) {
            val ch = reader.peekOrThrow()
            if (!validator(ch)) break
            sb.append(ch)
        }

        if (sb.length < minLength)
            throw FormatException("Expected at least $minLength digits to form a Unicode character escape sequence, found ${sb.length}.")
        val result = sb.toString().toInt(radix)
        return result.toChar()
    }

    /**
     * Reads a placeholder.
     *
     * The reader must be positioned at the opening `<` character
     * of the placeholder, and will be positioned at the character following the closing `>`
     * character of the placeholder or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readPlaceholder(reader: PushbackReader): Term = reader.run {
        error("Placeholders not supported")
//        readExpected("placeholder start", '<')
//        val template = readTerm(reader) ?: throw FormatException("Unexpected end of stream.")
//        readWhitespace(reader)
//        readExpected("placeholder end", '>')
//        val annotations = readAnnotations(reader)
//        return termFactory.newPlaceholder(template, TermAttachments.of(annotations))
    }

    /**
     * Reads a number (integer or real).
     *
     * The reader must be positioned at the opening digit
     * of the number, and will be positioned at the character following the number
     * or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readNumber(reader: PushbackReader): Term = reader.run {
        val ints: String = readDigits(reader)
        var frac = ""
        var exp = ""

        if (tryPeek('.') != null) {
            readExpected('.')
            frac = readDigits(reader)
        }
        if (tryPeek('e', 'E') != null) {
            readExpected('e', 'E')
            exp = readDigits(reader)
        }

        if (ints.isEmpty() && frac.isEmpty())
            throw FormatException("Expected a number, got something else.")

        val attachments = readAttachments(reader)

        return if (frac.isNotEmpty() || exp.isNotEmpty()) {
            val value = (
                    (ints.ifBlank { "0" }) + "." +
                            (frac.ifBlank { "0" }) + "e" +
                            (exp.ifBlank { "0" })
                    ).toDouble()
            termFactory.newReal(value, attachments)
        } else {
            val value = ints.toInt()
            termFactory.newInt(value, attachments)
        }
    }

    /**
     * Reads a constructor application.
     *
     * The reader must be positioned at the opening character
     * of the constructor name, and will be positioned at the character following the closing `)`
     * character of the constructor application or the closing `}` bracket of its annotations (if any).
     *
     * @param reader the reader to read from
     * @return the read term
     */
    private fun readAppl(reader: PushbackReader): ApplTerm = reader.run {
        val constructor = readIdentifier(reader)
        readWhitespace(reader)
        val ch = peekOrThrow()
        val terms: List<Term> = if (ch == '(') {
            readExpected('(')
            readTermSequence(reader, ',', ')')
        } else {
            emptyList()
        }

        val attachments = readAttachments(reader)
        return termFactory.newAppl(constructor, terms, attachments)
    }

    /**
     * Reads term attachments.
     *
     * Currently only annotations are supported as term attachments.
     *
     * @param reader the reader to read from
     * @return the read term attachments
     */
    private fun readAttachments(reader: PushbackReader): TermAttachments = reader.run {
        val annotations = readAnnotations(reader)
        if (annotations.isEmpty()) return TermAttachments.empty()
        return TermAttachments.of(TermAnnotationKey to annotations)
    }

    /**
     * Reads annotations.
     *
     * @param reader the reader to read from
     * @return a list of annotations
     */
    private fun readAnnotations(reader: PushbackReader): List<Term> = reader.run {
        if (tryPeek('{') == null) return emptyList()
        readExpected('{')
        return readTermSequence(reader, ',', '}')
    }

    /**
     * Reads an identifier.
     *
     * The reader must be positioned at the first character of the identifier,
     * and will be positioned at the first non-identifier character following the identifier.
     *
     * @param reader the reader to read from
     * @return the read identifier, which may be an empty string
     */
    private fun readIdentifier(reader: PushbackReader): String = reader.run {
        val sb = StringBuilder()

        var ch = peekOrThrow()
        if (isValidIdentifierFirstChar(ch)) {
            ch = reader.readOrThrow()
            sb.append(ch)

            while (tryPeek { isValidIdentifierChar(it) } != null) {
                ch = readOrThrow()
                sb.append(ch)
            }
        }

        return sb.toString()
    }

    /**
     * Reads a sequence of digits.
     *
     * The reader must be positioned at the first digit,
     * and will be positioned at the first non-digit character following the digits.
     *
     * @param reader the reader to read from
     * @return the read numbers, which may be an empty string
     */
    private fun readDigits(reader: PushbackReader): String = reader.run {
        val sb = StringBuilder()

        var ch = peekOrThrow()
        while (ch.isDigit()) {
            ch = reader.readOrThrow()
            sb.append(ch)

            ch = reader.peek() ?: break
        }

        return sb.toString()
    }

    /**
     * Reads a sequence of terms.
     *
     * The [end] character is also consumed.
     *
     * @param reader the reader to read from
     * @param separator the separator character
     * @param end the end character
     * @return the read terms
     */
    private fun readTermSequence(reader: PushbackReader, separator: Char, end: Char): List<Term> = reader.run {
        readWhitespace(reader)

        var ch = peekOrThrow()
        if (ch == end) {
            readExpected(end)
            return emptyList()
        }

        val terms = mutableListOf<Term>()
        do {
            val term = readTerm(reader) ?: throw FormatException("Unexpected end of stream.")
            terms.add(term)

            readWhitespace(reader)
            ch = readOrThrow()
        } while (ch == separator)

        if (ch != end)
            throw FormatException("Tern sequence didn't end with $end: $ch")

        return terms
    }

    /**
     * Reads whitespace.
     *
     * Reads any whitespace until there is no more whitespace to read.
     * The reader will be positioned at a non-whitespace character
     * or the end of the stream.
     *
     * @param reader the reader to read from
     * @return `true` when there is more to read;
     * otherwise, `false` when the end of the stream has been reached
     */
    private fun readWhitespace(reader: PushbackReader): Boolean {
        var ch: Char = reader.peek() ?: return false

        while (ch.isWhitespace()) {
            // Consume the character
            reader.read()

            // Peek the next character
            ch = reader.peek() ?: return false
        }
        return true
    }

    /**
     * Determines whether the character is valid
     * as the first character of an identifier.
     */
    private fun isValidIdentifierFirstChar(ch: Char): Boolean {
        return ch.isLetter() || ch in arrayOf('_', '-', '+', '*', '$')
    }

    /**
     * Determines whether the character is valid
     * as a character of an identifier.
     */
    private fun isValidIdentifierChar(ch: Char): Boolean {
        return ch.isLetterOrDigit() || ch in arrayOf('_', '-', '+', '*', '$')
    }

    /**
     * Peeks a single character.
     *
     * @return the read character;
     * or `null` when the end of the stream has been reached
     */
    private fun PushbackReader.peek(): Char? {
        val ch = this.read()
        if (ch == -1) return null
        this.unread(ch)
        return ch.toChar()
    }

    /**
     * Peeks a single character,
     * or throws when the end of the stream has been reached.
     *
     * @return the read character
     */
    private fun PushbackReader.peekOrThrow(): Char {
        return this.peek() ?: throw FormatException("Unexpected end of file.")
    }

    /**
     * Reads a single character,
     * or throws when the end of the stream has been reached.
     *
     * @return the read character
     */
    private fun PushbackReader.readOrThrow(): Char {
        val ch = this.read()
        if (ch == -1) throw FormatException("Unexpected end of file.")
        return ch.toChar()
    }

    /**
     * Expects to read a specific character from the reader;
     * or throws an exception when the read character was different,
     * or when the end of the stream has been reached.
     *
     * @param expected the expected character
     * @return the read character
     */
    private fun PushbackReader.readExpected(vararg expected: Char): Char {
        return readExpected(null) { ch -> ch in expected }
    }

    /**
     * Expects to read a specific character from the reader;
     * or throws an exception when the read character was different,
     * or when the end of the stream has been reached.
     *
     * @param expectedName the name of the expected character; or `null`
     * @param expected the expected character
     * @return the read character
     */
    private fun PushbackReader.readExpected(expectedName: String? = null, vararg expected: Char): Char {
        return readExpected(expectedName) { ch -> ch in expected }
    }

    /**
     * Expects to read a character from the reader that matches the given predicate;
     * or throws an exception when the read character was different,
     * or when the end of the stream has been reached.
     *
     * @param expectedName the name of the expected character; or `null`
     * @param predicate the predicate that tests the character
     * @return the read character
     */
    private fun PushbackReader.readExpected(expectedName: String? = null, predicate: (Char) -> Boolean): Char {
        val ch = readOrThrow()
        if (!predicate.invoke(ch)) {
            val message = if (expectedName != null)
                "Expected $expectedName, got '$ch' character."
            else
                "Did not expect '$ch' character."
            throw FormatException(message)
        }
        return ch
    }

    /**
     * Tries to peek to see if the given character matches the next character.
     *
     * @param expected the expected character
     * @return the next character; or `null` when the character was different
     */
    private fun PushbackReader.tryPeek(vararg expected: Char): Char? {
        return tryPeek { ch -> ch in expected }
    }

    /**
     * Tries to peek to see if the predicate is true for the next character.
     *
     * @param predicate the predicate
     * @return the next character; or `null` when the predicate failed
     */
    private fun PushbackReader.tryPeek(predicate: (Char) -> Boolean): Char? {
        val ch = peek() ?: return null
        return if (predicate(ch)) ch else null
    }

    /**
     * Determines whether the digit is a decimal digit.
     */
    private fun Char.isDecDigit()
            = this in arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    /**
     * Determines whether the digit is a hexadecimal digit.
     */
    private fun Char.isHexDigit()
            = this in arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F')
}

