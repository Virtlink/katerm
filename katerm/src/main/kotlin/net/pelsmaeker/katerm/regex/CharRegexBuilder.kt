package net.pelsmaeker.katerm.regex

/**
 * A builder for character-based regular expressions.
 *
 * @param R The type of regex being built.
 * @property builder The underlying regex builder.
 */
class CharRegexBuilder<R : Regex<Char, Unit>>(
    builder: RegexBuilder<R, Char, Unit>,
) : RegexBuilder<R, Char, Unit> by builder {

    operator fun Char.invoke(): R = atom(EqualityMatcher(this))

    operator fun Set<Char>.invoke(): R = atom(SetEqualityMatcher(this))

    operator fun Iterable<Char>.invoke(): R = atom(SetEqualityMatcher(this.toSet()))

}