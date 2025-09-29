package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution

/**
 * A matcher for regular expressions.
 *
 * @param T The type of input tokens to match against.
 * @param M The type of metadata associated with each state.
 */
interface RegexMatcher<T, M> {

    /**
     * Gets the metadata associated with an accepting state.
     *
     * @return The metadata associated with an accepting state;
     * otherwise, `null` if the matcher is not in an accepting state.
     */
    fun getAcceptingMetadata(): M?

    /**
     * Matches against the specified input token.
     *
     * @param input The token to match against.
     * @return A matcher representing the state after matching the token,
     * which is empty if the match failed.
     */
    fun match(input: T): RegexMatcher<T, M>

    /**
     * Matches against the specified sequence of input tokens.
     *
     * @param inputs The tokens to match against.
     * @return A matcher representing the state after matching the tokens,
     * which is empty if any of the matches failed.
     */
    fun matchAll(inputs: Iterable<T>): RegexMatcher<T, M> {
        var currentMatcher = this
        for (input in inputs) {
            val newMatcher = currentMatcher.match(input)
            if (newMatcher.isEmpty()) return newMatcher
            currentMatcher = newMatcher
        }
        return currentMatcher
    }

    /**
     * Whether the matcher is in any accepting state.
     */
    fun isAccepting(): Boolean

    /**
     * Whether the matcher is empty.
     */
    fun isEmpty(): Boolean

}