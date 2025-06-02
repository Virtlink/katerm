package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.terms.Term

/**
 * Matches terms.
 */
fun interface Matcher<in I: Term, out O: Term> {
    /**
     * Applies the matcher to the specified term
     *
     * @return The result of the match, or `null` if the term does not match.
     */
    fun match(term: I): O?


    fun <R: Term> map(f: (O) -> R): Matcher<I, R> = Matcher { t -> match(t)?.let(f) }
    fun filter(f: (O) -> Boolean): Matcher<I, O> = Matcher { t -> match(t)?.takeIf { f(it) } }

}

//fun <I: Term, O: Term, R: Term> Matcher<I, O>.map(f: (O) -> R): Matcher<I, R> = Matcher { t -> match(t)?.let(f) }
//fun <I: Term, O: Term> Matcher<I, O>.filter(f: (O) -> Boolean): Matcher<I, O> = Matcher { t -> match(t)?.takeIf { f(it) } }

object M {
    fun <T: Term> id(): Matcher<T, T> = Matcher { t -> t }
    fun <T: Term> fail(): Matcher<T, Nothing> = Matcher { null }

    fun term(): Matcher<Term, Term> = id()
    fun <I: Term, O: Term, R: Term> term(m: Matcher<I, O>, f: (I, O) -> R): Matcher<I, R> = Matcher { t -> m.match(t)?.let { f(t, it) } }

}