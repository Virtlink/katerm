package net.pelsmaeker.katerm.regex

/**
 * Represents a regular expression AST for testing, but has no logic for actual matching.
 */
sealed class RegexAst: Regex<String, Unit> {
    override fun buildMatcher(initialMetadata: Unit): RegexMatcher<String, Unit> {
        throw NotImplementedError("TestRegex does not implement buildMatcher")
    }

    data object Epsilon: RegexAst() {
        override fun toString(): String = "ε"
    }

    data class Atom(val matcher: Matcher<String, Unit>): RegexAst() {
        override fun toString(): String = "$matcher"
    }
    data class Concat(val first: RegexAst, val second: RegexAst): RegexAst() {
        override fun toString(): String = "($first $second)"
    }
    data class Union(val left: RegexAst, val right: RegexAst): RegexAst() {
        override fun toString(): String = "($left|$right)"
    }
    data class Star(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr*)"
    }
    data class Plus(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr+)"
    }
    data class Question(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr?)"
    }
    data class StarLazy(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr*?)"
    }
    data class PlusLazy(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr+?)"
    }
    data class QuestionLazy(val expr: RegexAst): RegexAst() {
        override fun toString(): String = "($expr??)"
    }

    object Builder: RegexBuilder<RegexAst, String, Unit> {
        operator fun String.invoke(): RegexAst = atom(EqualityMatcher(this))

        override fun epsilon(): RegexAst = Epsilon

        override fun atom(matcher: Matcher<String, Unit>): RegexAst = Atom(matcher)

        override fun concat(
            first: RegexAst,
            second: RegexAst,
        ): RegexAst = Concat(first, second)

        override fun union(
            first: RegexAst,
            second: RegexAst,
        ): RegexAst = Union(first, second)

        override fun star(pattern: RegexAst): RegexAst = Star(pattern)

        override fun starLazy(pattern: RegexAst): RegexAst = StarLazy(pattern)

        override fun plus(pattern: RegexAst): RegexAst = Plus(pattern)

        override fun plusLazy(pattern: RegexAst): RegexAst = PlusLazy(pattern)

        override fun question(pattern: RegexAst): RegexAst = Question(pattern)

        override fun questionLazy(pattern: RegexAst): RegexAst = QuestionLazy(pattern)

    }
}
