package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.io.DefaultTermWriter
import java.io.Writer
import java.lang.StringBuilder
import java.util.Objects

/**
 * Base class for [Term] implementations.
 *
 * @property termAttachments The term attachments associated with this term.
 */
abstract class TermBase(
    override val termAttachments: TermAttachments = TermAttachments.empty(),
): Term {

    /**
     * Whether the term has separators.
     *
     * Please override this property to provide a more efficient implementation.
     */
    // Please override to provide a more efficient implementation.
    override val hasTermSeparators: Boolean get() = super.hasTermSeparators

    /**
     * Creates a copy of this term with the specified new attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    abstract override fun withAttachments(newAttachments: TermAttachments): TermBase

    /**
     * Creates a copy of this term with the specified new separators.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param newSeparators The new separators of the term; or `null` to use (or reset to) the default separators.
     * @return The copy of the term, but with the new separators.
     */
    abstract override fun withSeparators(newSeparators: List<String>?): TermBase

    /**
     * Determines whether this term and its subterms represent the same value
     * as the given term and it subterms, regardless of the actual implementations
     * of the terms and its subterms.
     *
     * Note that attachments are also checked by this method.
     *
     * Implementations should compare equal to other implementations of the same term type,
     * but can take shortcuts when comparing to the same implementation of the term type.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true                         // Identity equality
        val that = other as? TermBase ?: return false
        // @formatter:off
        return this::class.java == that::class.java             // Same type
            && this.hash == that.hash                       // Same hash code (cheap check)
            && equals(that)                                 // Same subterms
            && this.termAttachments == that.termAttachments // Same attachments
            && this.termSeparators == that.termSeparators   // Same separators
        // @formatter:on
    }

    /**
     * Checks whether this term and the given term are equal.
     *
     * Please override this method to provide a more efficient implementation.
     * The type, hash equality, term attachments, and term separators have already been checked.
     *
     * @param that The term to check.
     * @return `true` if this term is equal to the specified term; otherwise, `false`.
     */
    protected abstract fun equals(that: TermBase): Boolean

    /**
     * Returns the hash code of the term, include the hash of the attachments.
     *
     * This cannot be overridden. Instead, implement the [hash] property
     * by performing an eager hash calculation that includes the hash of the attachments.
     */
    final override fun hashCode(): Int = hash

    /**
     * Implement this property to perform a custom hash code calculation.
     * Do include the attachments and separators.
     *
     * Please override this property to provide a more efficient implementation.
     */
    protected open val hash: Int = Objects.hash(
        this.termChildren,
        this.termAttachments,
        this.termSeparators,
    )

    final override fun toString(): String = printer.writeToString(this)

    companion object {
        /** The default term writer used for [toString]. */
        private val printer = DefaultTermWriter(
            format = DefaultTermWriter.Format.AUTO,
        )
    }
}