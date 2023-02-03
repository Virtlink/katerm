package net.pelsmaeker.katerm

/**
 * Transforms terms.
 */
fun interface Strategy2<in I: Term, out O: Term> {
    /**
     * Applies the strategy to the specified term
     *
     * @return the result of the strategy; or `null` if the term did not match
     */
    fun apply(term: I): O?
}

// TODO: How to create a strategy without providing a value?

// Initials

/** Identity. */
fun <A: Term> id(): Strategy2<A, A> = Strategy2 { t -> t }
/** Always fails. */
fun <A: Term> fail(): Strategy2<A, Nothing> = Strategy2 { null }
/** Succeeds only if the term matches the specified term. */ // TODO: Patterns?
fun <A: Term> match(other: A): Strategy2<Term, A> = Strategy2 { t -> t.takeIf{ it.matches(other) } as A? }
/** Builds a term, ignoring the input term. */
fun <A: Term> build(f: () -> A): Strategy2<Term, A> = Strategy2 { _ -> f() }

/** Maps a function over a value. */
fun <A: Term, B: Term> map(f: (A) -> B?): Strategy2<A, B> = Strategy2 { t -> f(t) }
/** Succeeds only if the filter matches. */
fun <A: Term> filter(f: (A) -> Boolean): Strategy2<A, A> = Strategy2 { t -> t.takeIf { f(it) } }
/** Succeeds only if the strategy succeeds. */
fun <A: Term> where(f: Strategy2<A, Term>): Strategy2<A, A> = Strategy2 { t -> t.takeIf { f.apply(it) != null } }
/** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
fun <A: Term, B: Term, C: Term> glc(condition: Strategy2<A, B>, onSuccess: Strategy2<B, C>, onFail: Strategy2<A, C>): Strategy2<A, C> = Strategy2 { t -> t?.let { condition.apply(it)?.let { it2 -> onSuccess.apply(it2) } ?: onFail.apply(it) } }

/** Succeeds only if the term is an application term. */
fun isAppl(): Strategy2<Term, ApplTerm> = Strategy2 { t -> t as? ApplTerm }
/** Succeeds only if the term is an application term of the specified type. */
fun isAppl(type: ApplTermType): Strategy2<Term, ApplTerm> = Strategy2 { t -> (t as? ApplTerm)?.takeIf { it.type == type } }

/** Succeeds only if the term is an integer term. */
fun isInt(): Strategy2<Term, IntTerm> = Strategy2 { t -> t as? IntTerm }

/** Succeeds only if the term is a string term. */
fun isString(): Strategy2<Term, StringTerm> = Strategy2 { t -> t as? StringTerm }

/** Succeeds only if the term is a blob term. */
fun isBlob(): Strategy2<Term, BlobTerm> = Strategy2 { t -> t as? BlobTerm }

/** Succeeds only if the term is a list term. */
fun isList(): Strategy2<Term, ListTerm> = Strategy2 { t -> t as? ListTerm }
/** Succeeds only if the term is a list term of the specified type. */
fun isList(type: ListTermType): Strategy2<Term, ListTerm> = Strategy2 { t -> (t as? ListTerm)?.takeIf { it.type == type } }

/** Succeeds only if the term is a var term. */
fun isVar(): Strategy2<Term, TermVar> = Strategy2 { t -> t as? TermVar }
/** Succeeds only if the term is a var term of the specified type. */
fun isVar(type: TermType): Strategy2<Term, TermVar> = Strategy2 { t -> (t as? TermVar)?.takeIf { it.type == type } }

// Continuations

/** Identity. */
fun <A: Term> Strategy2<Term, A>.id(): Strategy2<Term, A> = this
/** Always fails. */
fun Strategy2<Term, Term>.fail(): Strategy2<Term, Nothing> = Strategy2 { null }
/** Succeeds only if the term matches the specified term. */ // TODO: Patterns?
fun <A: Term> Strategy2<Term, A>.match(other: A): Strategy2<Term, A> = Strategy2 { t -> apply(t)?.takeIf{ it.matches(other) } as A? }
/** Builds a term, ignoring the input term. */
fun <A: Term> Strategy2<Term, A>.build(f: () -> A): Strategy2<Term, A> = Strategy2 { _ -> f() }

/** Maps a function over a value. */
fun <A: Term, B: Term> Strategy2<Term, A>.map(f: (A) -> B?): Strategy2<A, B> = Strategy2 { t -> apply(t)?.let { f(it) } }
/** Succeeds only if the filter matches. */
fun <A: Term> Strategy2<Term, A>.filter(f: (A) -> Boolean): Strategy2<Term, A> = Strategy2 { t -> apply(t)?.takeIf { f(it) } }
/** Succeeds only if the strategy succeeds. */
fun <A: Term, B: Term> Strategy2<Term, A>.where(f: Strategy2<A, B>): Strategy2<Term, A> = Strategy2 { t -> apply(t)?.takeIf { f.apply(it) != null } }
/** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
fun <A: Term, B: Term, C: Term> Strategy2<Term, A>.glc(condition: Strategy2<A, B>, onSuccess: Strategy2<B, C>, onFail: Strategy2<A, C>): Strategy2<Term, C> = Strategy2 { t -> apply(t)?.let { condition.apply(it)?.let { it2 -> onSuccess.apply(it2) } ?: onFail.apply(it) } }

/** Succeeds only if the term is an application term. */
fun Strategy2<Term, Term>.isAppl(): Strategy2<Term, ApplTerm> = Strategy2 { t -> apply(t) as? ApplTerm }
/** Succeeds only if the term is an application term of the specified type. */
fun Strategy2<Term, Term>.isAppl(type: ApplTermType): Strategy2<Term, ApplTerm> = Strategy2 { t -> (apply(t) as? ApplTerm)?.takeIf { it.type == type } }


/** Succeeds only if the term is an integer term. */
fun Strategy2<Term, Term>.isInt(): Strategy2<Term, IntTerm> = Strategy2 { t -> apply(t) as? IntTerm }

/** Succeeds only if the term is a string term. */
fun Strategy2<Term, Term>.isString(): Strategy2<Term, StringTerm> = Strategy2 { t -> apply(t) as? StringTerm }

/** Succeeds only if the term is a blob term. */
fun Strategy2<Term, Term>.isBlob(): Strategy2<Term, BlobTerm> = Strategy2 { t -> apply(t) as? BlobTerm }

/** Succeeds only if the term is a list term. */
fun Strategy2<Term, Term>.isList(): Strategy2<Term, ListTerm> = Strategy2 { t -> apply(t) as? ListTerm }
/** Succeeds only if the term is a list term of the specified type. */
fun Strategy2<Term, Term>.isList(type: ListTermType): Strategy2<Term, ListTerm> = Strategy2 { t -> (apply(t) as? ListTerm)?.takeIf { it.type == type } }

/** Succeeds only if the term is a var term. */
fun Strategy2<Term, Term>.isVar(): Strategy2<Term, TermVar> = Strategy2 { t -> apply(t) as? TermVar }
/** Succeeds only if the term is a var term of the specified type. */
fun Strategy2<Term, Term>.isVar(type: TermType): Strategy2<Term, TermVar> = Strategy2 { t -> (apply(t) as? TermVar)?.takeIf { it.type == type } }

// Terminals

fun main() {
    val builder = DefaultTermBuilder()
    val x = build { builder.createInt(10) }.glc(
        { t -> builder.createString(t.value.toString()) },
        { t -> builder.createInt(t.value.toInt()) },
        build { builder.createString("40") }
    ).fail()

    println(x.apply(builder.createInt(10)))
}