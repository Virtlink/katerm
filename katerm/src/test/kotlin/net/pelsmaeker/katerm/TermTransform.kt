package net.pelsmaeker.katerm//package net.pelsmaeker.katerm
//
///**
// * Transforms terms.
// */
//fun interface Transform<in I: Term, out O: Term> {
//    /**
//     * Applies the transform to the specified term
//     *
//     * @return the result of the transform; or `null` if the term did not match
//     */
//    fun apply(term: I): O?
//
////    fun <R: Term> map(f: (O) -> R): Transform<I, R> = Transform { t -> f(apply(t)) }
////    fun filter(f: (O) -> Boolean): Transform<I, O> = Transform { t -> apply(t).takeIf { f(it) } ?: t }
//}
//
//object T {
//    /** A transform that does nothing. */
//    fun <T: Term> id(): Transform<T, T> = Transform { t -> t }
//    /** A transform that always fails. */
//    fun <T: Term> fail(): Transform<T, Nothing> = Transform { null }
//
//    /** A transform that succeeds only if the term is an application term. */
//    fun isAppl(): Transform<Term, ApplTerm> = Transform { t -> t as? ApplTerm }
//    /** A transform that succeeds only if the term is an application term of the specified type. */
//    fun isAppl(type: ApplTermType): Transform<Term, ApplTerm> = Transform { t -> (t as? ApplTerm)?.takeIf { it.type == type } }
//
//    /** A transform that succeeds only if the term is an integer term. */
//    fun isInt(): Transform<Term, IntTerm> = Transform { t -> t as? IntTerm }
//    /** A transform that succeeds only if the term is a string term. */
//    fun isString(): Transform<Term, StringTerm> = Transform { t -> t as? StringTerm }
//    /** A transform that succeeds only if the term is a blob term. */
//    fun isBlob(): Transform<Term, BlobTerm> = Transform { t -> t as? BlobTerm }
//
//    /** A transform that succeeds only if the term is a list term. */
//    fun isList(): Transform<Term, ListTerm> = Transform { t -> t as? ListTerm }
//    /** A transform that succeeds only if the term is a list term of the specified type. */
//    fun isList(type: ListTermType): Transform<Term, ListTerm> = Transform { t -> (t as? ListTerm)?.takeIf { it.type == type } }
//
//    /** A transform that succeeds only if the term is a var term. */
//    fun isVar(): Transform<Term, TermVar> = Transform { t -> t as? TermVar }
//    /** A transform that succeeds only if the term is a var term of the specified type. */
//    fun isVar(type: TermType): Transform<Term, TermVar> = Transform { t -> (t as? TermVar)?.takeIf { it.type == type } }
//
//}
//
//fun test() {
//
//}