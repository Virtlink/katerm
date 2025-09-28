package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.substitutions.unify
import net.pelsmaeker.katerm.terms.Term

fun interface Matcher {
    fun matches(input: Term, substitution: Substitution): Substitution?
}


data class EqualityMatcher(
    val expected: Term,
) : Matcher {
    override fun matches(input: Term, substitution: Substitution): Substitution? {
        return if (input == expected) substitution else null
    }
}

data class UnifyingMatcher(
    val expected: Term,
) : Matcher {
    override fun matches(input: Term, substitution: Substitution): Substitution? {
        return substitution.unify(expected, input)
    }
}