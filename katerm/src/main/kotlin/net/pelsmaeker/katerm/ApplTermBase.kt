package net.pelsmaeker.katerm

import java.util.Objects

/**
 * Base class for [ApplTerm] implementations.
 *
 * @property termAttachments The term attachments associated with this term.
 */
abstract class ApplTermBase(
    termAttachments: TermAttachments = TermAttachments.empty(),
): ApplTerm, TermBase(termAttachments) {

    /**
     * The constructor name.
     *
     * By default the constructor name is the simple class name.
     * Override this implementation to provide a custom constructor name.
     */
    // Please override to provide a custom implementation.
    override val termOp: String get() = this::class.java.simpleName

    /**
     * Creates a copy of this term with the specified new attachments.
     *
     * Calling this method can be more efficient than deconstructing and rebuilding a term.
     *
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    abstract override fun withAttachments(newAttachments: TermAttachments): ApplTermBase

    final override fun equals(that: TermBase): Boolean {
        if (that !is ApplTermBase) return false
        return this.termOp == that.termOp
          && equalSubterms(that)
    }

    /**
     * Checks whether this term and the given term have equal subterms.
     *
     * Please override this method to provide a more efficient implementation.
     *
     * @param that The term to check.
     * @return `true` if this term has the same subterms as the specified term; otherwise, `false`.
     */
    protected open fun equalSubterms(that: ApplTermBase): Boolean {
        // Please override to provide a more efficient implementation.
        return this.termArgs == that.termArgs
    }

    /**
     * Implement this property to perform a custom hash code calculation.
     * Do include the attachments and separators.
     *
     * Please override this property to provide a more efficient implementation.
     */
    override val hash: Int get() = Objects.hash(
        this.termChildren,
        this.termAttachments,
    )
}