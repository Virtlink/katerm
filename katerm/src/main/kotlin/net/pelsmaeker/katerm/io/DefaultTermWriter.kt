package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.*
import java.io.Writer

/**
 * Prints a term as a string.
 */
class DefaultTermWriter(
    // The configuration of the term printer.
    /** The maximum depth of the tree to print; or -1 to print all. */
    private val maxDepth: Int = -1,
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

    /**
     * The state of the current printing session.
     */
    private inner class Visitor(
        /** The writer to print to. */
        private val writer: Writer,
    ): TermVisitor<Unit> {

        // The remaining depth to print; or -1 to print all.
        private var depth = maxDepth

        override fun visitInt(term: IntTerm) = writer.run {
            // Print the integer value.
            write(term.value.toString())
        }

        override fun visitReal(term: RealTerm) = writer.run {
            // Print the real value.
            write(term.value.toString())
        }

        override fun visitString(term: StringTerm) = writer.run {
            // Print the escaped string.
            write('"'.code)
            write(escape(term.value))
            write('"'.code)
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

        override fun visitAppl(term: ApplTerm) = writer.run {
            write(term.termOp)
            write('('.code)
            writeSubtermList(term.termArgs)
            write(')'.code)
        }

        override fun visitList(term: ListTerm) = writer.run {
            write('['.code)
            writeSubtermList(term.elements)
            write(']'.code)
        }

        override fun visitVar(term: TermVar) = writer.run {
            // Print something like "?x@resource", or "?x" if there is no resource.
            write('?'.code)
            write(term.name)
            term.resource?.let {
                write('@'.code)
                write(it)
            } ?: Unit
        }

        // TODO: Optimize to write the escaped string immediately to the writer
        private fun escape(s: String): String = s   // TODO: Optimize to go over the string only once
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
        // TODO: Escape non-printable characters

        /**
         * Writes a comma-separated list of subterms.
         *
         * @param collection the collection of subterms
         */
        private fun Writer.writeSubtermList(collection: Collection<Term>) {
            // This ensures we will write an empty list, even if we reached the maximum depth
            if (collection.isEmpty()) {
                // The collection is empty. Write an empty list, even if we reached the maximum depth.
                return
            } else if (depth > 0) {
                // The collection is not empty and we haven't reached the maximum depth yet.
                depth -= 1
                collection.iterator().run {
                    next().accept(this@Visitor)
                    while (hasNext()) {
                        write(", ")
                        next().accept(this@Visitor)
                    }
                }
                depth += 1
            } else {
                // We've reached the maximum depth.
                // Write something like "..3 terms.." to indicate that terms were elided
                write("..")
                write(collection.size)
                write(' '.code)
                write("terms")
                write("..")
            }
        }
    }

}