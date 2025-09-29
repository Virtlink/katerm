package net.pelsmaeker.katerm.regex

/**
 * A builder for regular expressions.
 *
 * @param R The type of regular expression being built.
 */
@Suppress("PropertyName", "DANGEROUS_CHARACTERS")
interface RegexBuilder<R : Regex<T, M>, T, M> {

//    /**
//     * Creates a regular expression pattern matching the given term.
//     *
//     * @param term The term to match.
//     * @return The built regular expression.
//     */
//    @Suppress("FunctionName")
//    fun T(term: Term): R = atom(UnifyingMatcher(term))

    /**
     * Creates a regular expression pattern matching any term.
     *
     * @return The built regular expression.
     */
    fun any(): R = atom(WildcardMatcher<T, M>())

    /**
     * Creates a regular expression pattern matching the concatenation of the first and second patterns.
     *
     * This operator is left-associative, meaning that `a .. b .. c` is interpreted as `(a .. b) .. c`.
     *
     * @receiver The first pattern to concatenate.
     * @param second The second pattern to concatenate.
     * @return A new regex representing the concatenation of the two patterns.
     */
    operator fun R.rangeTo(second: R): R = concat(this, second)

    /**
     * Creates a regular expression pattern matching left pattern and otherwise the right pattern.
     *
     * This operator is left-associative, meaning that `a and b and c` is interpreted as `(a and b) and c`.
     *
     * @receiver The left pattern to match.
     * @param other The right pattern to match.
     * @return A new regex representing the disjunction of the two patterns.
     */
    infix fun R.or(other: R): R = union(this, other)

    /** Creates a regular expression pattern matching a sequence of zero or more of the given pattern (greedy). */
    val R.`*`: R get() = star(this)
    /** Creates a regular expression pattern matching a sequence of one or more of the given pattern (greedy). */
    val R.`+`: R get() = plus(this)
    /** Creates a regular expression pattern matching a sequence of zero or one of the given pattern (greedy). */
    val R.`?`: R get() = question(this)

    /** Creates a regular expression pattern matching a sequence of zero or more of the given pattern (lazy). */
    val R.`*?`: R get() = starLazy(this)
    /** Creates a regular expression pattern matching a sequence of one or more of the given pattern (lazy). */
    val R.`+?`: R get() = plusLazy(this)
    /** Creates a regular expression pattern matching a sequence of zero or one of the given pattern (lazy). */
    val R.`??`: R get() = questionLazy(this)
    
    /**
     * Creates a regular expression pattern matching using the given matcher.
     *
     * @param matcher The matcher to use for matching.
     * @return The built regular expression.
     */
    fun atom(matcher: Matcher<T, M>): R

    /**
     * Creates a regular expression pattern concatenating the first and second patterns.
     *
     * @param first The first pattern to concatenate.
     * @param second The second pattern to concatenate.
     * @return The built regular expression.
     */
    fun concat(first: R, second: R): R

    /**
     * Creates a regular expression pattern matching either the first or the second pattern.
     *
     * @param first The first pattern to match.
     * @param second The second pattern to match.
     * @return The built regular expression.
     */
    fun union(first: R, second: R): R

    /**
     * Creates a regular expression pattern matching the pattern zero or more times (greedy).
     *
     * @param pattern The pattern to match zero or more times.
     * @return The built regular expression.
     */
    fun star(pattern: R): R

    /**
     * Creates a regular expression pattern matching the pattern zero or more times (lazy).
     *
     * @param pattern The pattern to match zero or more times.
     * @return The built regular expression.
     */
    fun starLazy(pattern: R): R

    /**
     * Creates a regular expression pattern matching the pattern one or more times (greedy).
     *
     * @param pattern The pattern to match one or more times.
     * @return The built regular expression.
     */
    fun plus(pattern: R): R

    /**
     * Creates a regular expression pattern matching the pattern one or more times (lazy).
     *
     * @param pattern The pattern to match one or more times.
     * @return The built regular expression.
     */
    fun plusLazy(pattern: R): R

    /**
     * Creates a regular expression pattern matching the pattern zero or one time (greedy).
     *
     * @param pattern The pattern to match zero or one time.
     * @return The built regular expression.
     */
    fun question(pattern: R): R

    /**
     * Creates a regular expression pattern matching the pattern zero or one time (lazy).
     *
     * @param pattern The pattern to match zero or one time.
     * @return The built regular expression.
     */
    fun questionLazy(pattern: R): R

}