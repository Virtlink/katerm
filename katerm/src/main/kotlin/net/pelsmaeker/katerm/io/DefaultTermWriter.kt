package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.*
import java.io.Writer

/**
 * Prints a term as a string.
 *
 * @property format The format in which to print the term.
 * @property maxDepth The maximum depth of the tree to print; or -1 to print all.
 */
class DefaultTermWriter(
    // The configuration of the term printer.
    val format: Format = Format.AUTO,
    val maxDepth: Int = -1,
    // TODO: Print multiline pretty.
//    /** Whether to print a multiline tree. */
//    private val multiline: Boolean = true,
    // TODO: Print attachments.
//    /** Whether to print attachments. */
//    private val printAttachments: Boolean = true,
): TermTextWriter {

    override fun write(term: Term, writer: Writer) {
        term.accept(Visitor(writer))
    }

    /** Specifies the format in which to print the term. */
    enum class Format {
        /** Prints the term as [TEXT], unless it has no term separators specified, in which case it is printed as [TERM]. */
        AUTO,
        /** Prints the term as a text string with layout and separators. */
        TEXT,
        /** Prints the term as a term tree. */
        TERM,
    }

    /**
     * The state of the current printing session.
     */
    private inner class Visitor(
        /** The writer to print to. */
        private val writer: Appendable,
    ): TermVisitor<Unit> {

        // The remaining depth to print; or -1 to print all.
        private var depth = maxDepth

        override fun visitInt(term: IntTerm): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                // Print the integer value.
                append(term.termValue.toString())
            }
        }

        override fun visitReal(term: RealTerm): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                // Print the real value.
                append(term.termValue.toString())
            }
        }

        override fun visitString(term: StringTerm): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                // Print the escaped string.
                append('"')
                append(escape(term.termValue))
                append('"')
            }
        }

//        override fun visitBlob(term: BlobTerm) = writer.run {
//            // Print something like "<blob(java.lang.String@12ab34)>"
//            val value = term.value
//            write("<blob(")
//            write(value::class.java.name)
//            write('@'.code)
//            write(term.value.hashCode().toString(16))
//            write(")>")
//        }

        override fun visitAppl(term: ApplTerm): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                append(term.termOp)
                append('(')
                writeSubtermList(term.termArgs)
                append(')')
            }
        }

        override fun visitList(term: ListTerm<Term>): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                append('[')
                writeSubtermList(term.elements)
                append(']')
            }
        }

        override fun visitVar(term: TermVar): Unit = writer.run {
            if (format == Format.TEXT || (format == Format.AUTO && term.hasTermSeparators)) {
                writeAsText(term)
            } else {
                // Print something like "?x@resource", or "?x" if there is no resource.
                append('?')
                append(term.name)
            }
        }

        // TODO: Optimize to write the escaped string immediately to the writer
        private fun escape(s: String): String = s   // TODO: Optimize to go over the string only once
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
        // TODO: Escape non-printable characters

        /**
         * Writes the term as a text string with layout and separators.
         *
         * @param term The term to write.
         */
        private fun writeAsText(term: Term): Unit = writer.run {
            term.appendString(writer) {
                it.accept(this@Visitor)
            }
        }

        /**
         * Writes a comma-separated list of subterms.
         *
         * @param collection the collection of subterms
         */
        private fun Appendable.writeSubtermList(collection: Collection<Term>) {
            // This ensures we will write an empty list, even if we reached the maximum depth
            if (collection.isEmpty()) {
                // The collection is empty. Write an empty list, even if we reached the maximum depth.
                return
            } else if (depth != 0) {
                // The collection is not empty and we haven't reached the maximum depth yet.
                depth -= 1
                collection.iterator().run {
                    next().accept(this@Visitor)
                    while (hasNext()) {
                        append(", ")
                        next().accept(this@Visitor)
                    }
                }
                depth += 1
            } else {
                // We've reached the maximum depth.
                // Write something like "..(3 terms).." to indicate that terms were elided
                append("..(${collection.size} terms)..")
            }
        }
    }

}