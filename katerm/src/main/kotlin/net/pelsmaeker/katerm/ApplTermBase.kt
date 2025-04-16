package net.pelsmaeker.katerm

/**
 * Base class for [ApplTerm] implementations.
 *
 * @property termAttachments The term attachments associated with this term.
 */
abstract class ApplTermBase(
    override val termAttachments: TermAttachments = TermAttachments.empty(),
): ApplTerm {

    /**
     * The constructor name.
     *
     * By default the constructor name is the simple class name.
     * Override this implementation to provide a custom constructor name.
     */
    // Please override to provide a custom implementation.
    override val termOp: String get() = this::class.java.simpleName

    /**
     * Whether the term has separators.
     *
     * Please override this property to provide a more efficient implementation.
     */
    // Please override to provide a more efficient implementation.
    open val hasTermSeparators: Boolean get() = termSeparators != null

    /**
     * Creates a copy of this term with the specified new attachments.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    abstract fun withAttachments(newAttachments: TermAttachments): ApplTermBase

    /**
     * Creates a copy of this term with the specified new separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param newSeparators The new separators of the term; or `null` to use (or reset to) the default separators.
     * @return The copy of the term, but with the new separators.
     */
    abstract fun withSeparators(newSeparators: List<String>?): ApplTermBase

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
        val that = other as? ApplTermBase ?: return false       // Must be an ApplTermBase
        // @formatter:off
        return this::class.java == that::class.java             // Same type
                && this.hash == that.hash                       // Same hash code (cheap check)
                && equalsAppl(that)                             // Same subterms
                && this.termAttachments == that.termAttachments // Same attachments
                && this.termSeparators == that.termSeparators   // Same separators
        // @formatter:on
    }

    /**
     * Checks whether this term and the given term are equal.
     *
     * Please override this method to provide a more efficient implementation.
     *
     * @param that The term to check.
     * @return `true` if this term is equal to the specified term; otherwise, `false`.
     */
    protected open fun equalsAppl(that: ApplTerm): Boolean {
        // Please override to provide a more efficient implementation.
        return this.termArgs == that.termArgs
    }

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
     */
    protected abstract val hash: Int

    /**
     * Returns a string representation of the term.
     *
     * Please override this method to provide a custom implementation.
     */
    override fun toString(): String {
        // Please override to provide a custom implementation.
        return "$termOp(${termArgs.joinToString(", ")})"
    }
}