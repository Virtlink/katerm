package net.pelsmaeker.katerm.io

import net.pelsmaeker.katerm.terms.ApplTerm
import net.pelsmaeker.katerm.terms.ConcatListTerm
import net.pelsmaeker.katerm.terms.ConsListTerm
import net.pelsmaeker.katerm.terms.IntTerm
import net.pelsmaeker.katerm.terms.ListTerm
import net.pelsmaeker.katerm.terms.NilListTerm
import net.pelsmaeker.katerm.terms.NoneOptionTerm
import net.pelsmaeker.katerm.terms.RealTerm
import net.pelsmaeker.katerm.terms.SomeOptionTerm
import net.pelsmaeker.katerm.terms.StringTerm
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar
import net.pelsmaeker.katerm.terms.TermVisitor
import java.io.Writer

/**
 * Prints a term as a string.
 *
 * @property maxDepth The maximum depth of the tree to print; or -1 to print all.
 */
class DefaultTermWriter(
    val maxDepth: Int = -1,
): TermTextWriter {

    override fun write(term: Term, writer: Writer) {
        term.accept(Visitor(writer))
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
            // Print the integer value.
            append(term.value.toString())
        }

        override fun visitReal(term: RealTerm): Unit = writer.run {
            // Print the real value.
            append(term.value.toString())
        }

        override fun visitString(term: StringTerm): Unit = writer.run {
            // Print the escaped string.
            append('"')
            append(escape(term.value))
            append('"')
        }

        override fun visitAppl(term: ApplTerm): Unit = writer.run {
            append(term.termOp)
            append('(')
            writeSubtermList(term.termArgs)
            append(')')
        }

        override fun visitConsList(term: ConsListTerm<Term>): Unit = writer.run {
            append("[")
            term.head.accept(this@Visitor)
            var current: ListTerm<Term> = term.tail
            while (true) {
                when (current) {
                    is ConsListTerm<*> -> {
                        append(", ")
                        current.head.accept(this@Visitor)
                        current = current.tail
                    }

                    is NilListTerm -> {
                        append("]")
                        break
                    }

                    is TermVar -> {
                        append(" | ")
                        current.accept(this@Visitor)
                        append("]")
                        break
                    }

                    else -> error("Unexpected term in list tail: $current")
                }
            }
        }

        override fun visitNilList(term: NilListTerm): Unit = writer.run {
            append("[]")
        }

        override fun visitConcatList(term: ConcatListTerm<Term>): Unit = writer.run {
            term.left.accept(this@Visitor)
            append(" ++ ")
            term.right.accept(this@Visitor)
        }

        override fun visitSomeOption(term: SomeOptionTerm<Term>): Unit = writer.run {
            append("<")
            term.element.accept(this@Visitor)
            append(">")
        }

        override fun visitNoneOption(term: NoneOptionTerm): Unit = writer.run {
            append("<>")
        }

        override fun visitVar(term: TermVar): Unit = writer.run {
            // Print something like "?x@resource", or "?x" if there is no resource.
            append('?')
            append(term.name)
        }

        // TODO: Merge this with TermBuilderBase.escape()
        // TODO: Optimize to write the escaped string immediately to the writer
        private fun escape(s: String): String = s   // TODO: Optimize to go over the string only once
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        // TODO: Escape non-printable characters

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