package net.pelsmaeker.katerm

/**
 * Builds terms.
 *
 * Some types of terms don't accept separators because they cannot be pretty-printed.
 */
interface TermBuilder {

    /**
     * Creates a copy of the specified term with the specified new attachments.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param T The type of term to copy.
     * @param term The term to copy.
     * @param newAttachments The new attachments of the term.
     * @return The copy of the term, but with the new attachments.
     */
    fun <T: Term> withAttachments(term: T, newAttachments: TermAttachments): T

    /**
     * Creates a copy of the specified term with the specified new separators.
     *
     * Calling this method can be efficient than deconstructing and rebuilding a term.
     *
     * @param T The type of term to copy.
     * @param term The term to copy.
     * @param newSeparators The new separators of the term; or `null` to use (or reset to) the default separators.
     * @return The copy of the term, but with the new separators.
     */
    fun <T: Term> withSeparators(term: T, newSeparators: List<String>?): T

}

