package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.substitutions.unify
import net.pelsmaeker.katerm.terms.Term

fun interface Matcher<in T, M> {
    /**
     * Tries to match the given input token.
     *
     * @param input The token to match against.
     * @param metadata Additional metadata associated with the current state, that may be used for matching.
     * @return The updated metadata if the match is successful; otherwise, `null`.
     */
    fun matches(input: T, metadata: M): M?
}

/**
 * A matcher that matches tokens for equality.
 *
 * @property expected The expected tokens to match against.
 */
data class EqualityMatcher<T, M>(
    val expected: T,
) : Matcher<T, M> {
    override fun matches(input: T, metadata: M): M? {
        return if (input == expected) metadata else null
    }

    override fun toString(): String = "$expected"
}

/**
 * A matcher that matches when a token is in the expected set.
 *
 * @property expected The expected tokens to match against.
 */
data class SetEqualityMatcher<T, M>(
    val expected: Set<T>,
) : Matcher<T, M> {
    override fun matches(input: T, metadata: M): M? {
        return if (input in expected) metadata else null
    }
}

/**
 * A matcher that matches terms by unifying them.
 *
 * @property expected The expected term to unify against.
 */
data class UnifyingMatcher(
    val expected: Term,
) : Matcher<Term, Substitution> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun matches(input: Term, substitution: Substitution): Substitution? {
        return substitution.unify(expected, input)
    }

    override fun toString(): String = "$expected"
}

/**
 * A matcher that matches any term, without modifying the substitution.
 */
class WildcardMatcher<T, M> : Matcher<T, M> {
    override fun matches(input: T, metadata: M): M? {
        return metadata
    }

    override fun toString(): String = "_"
}