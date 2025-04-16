package net.pelsmaeker.katerm

/**
 * Matches terms.
 */
fun interface TermMatcher<out R> {
    /**
     * Applies the matcher to the specified term
     *
     * @return the result of the match, or `null` if the term does not match
     */
    fun match(term: Term): R?


    fun <R: Term> map(f: (Term) -> R): TermMatcher<R> = TermMatcher { t -> match(t)?.let(f) }
    fun filter(f: (Term) -> Boolean): TermMatcher<R> = TermMatcher { t -> match(t)?.takeIf { f(it) } }

}

object M {
    fun term(): TermMatcher<Term> = TermMatcher { t ->
        t
    }
    fun <R> term(f: (Term) -> R): TermMatcher<R> = TermMatcher { t ->
        f(t)
    }
    fun <T: Term, R> term(m: TermMatcher<T>, f: (Term, T) -> R): TermMatcher<R> = TermMatcher { t ->
        m.match(t)?.let { f(t, it) }
    }
    // cases
    fun appl(): TermMatcher<ApplTerm> = TermMatcher { t ->
        t as? ApplTerm
    }
    fun <R> appl(f: (ApplTerm) -> R): TermMatcher<R> = TermMatcher { t ->
        (t as? ApplTerm)
            ?.let { f(it) }
    }
    fun <R> appl(op: String): TermMatcher<ApplTerm> = TermMatcher { t ->
        (t as? ApplTerm)
            ?.takeIf { it.termOp == op }
    }
    fun <R> appl(op: String, f: (ApplTerm) -> R): TermMatcher<R> = TermMatcher { t ->
        (t as? ApplTerm)
            ?.takeIf { it.termOp == op }
            ?.let { f(it) }
    }
}

//fun <I: Term, O: Term, R: Term> Matcher<I, O>.map(f: (O) -> R): Matcher<I, R> = Matcher { t -> match(t)?.let(f) }
//fun <I: Term, O: Term> Matcher<I, O>.filter(f: (O) -> Boolean): Matcher<I, O> = Matcher { t -> match(t)?.takeIf { f(it) } }
//
//object M {
//    fun <T: Term> id(): Matcher<T, T> = Matcher { t -> t }
//    fun <T: Term> fail(): Matcher<T, Nothing> = Matcher { null }
//
//    fun term(): Matcher<Term, Term> = id()
//    fun <I: Term, O: Term, R: Term> term(m: Matcher<I, O>, f: (I, O) -> R): Matcher<I, R> = Matcher { t -> m.match(t)?.let { f(t, it) } }
//
//}