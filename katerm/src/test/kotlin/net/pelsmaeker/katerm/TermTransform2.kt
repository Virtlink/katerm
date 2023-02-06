package net.pelsmaeker.katerm//package net.pelsmaeker.katerm
//
//object Transform2 {
//    /**
//     * Transforms terms.
//     */
//    fun interface Strategy<out O : Term> {
//        /**
//         * Applies the strategy to the specified term
//         *
//         * @return the result of the strategy; or `null` if the term did not match
//         */
//        fun apply(term: Term): O?
//    }
//
//// TODO: How to create a strategy without providing a value?
//
//// Initials
//
//    /** Identity. */
//    fun id(): Strategy<Term> = Strategy { t -> t }
//
//    /** Always fails. */
//    fun fail(): Strategy<Nothing> = Strategy { null }
//
//    /** Succeeds only if the term matches the specified term. */ // TODO: Patterns?
//    fun <A : Term> match(other: A): Strategy<A> = Strategy { t -> t.takeIf { it.matches(other) } as A? }
//
//    /** Builds a term, ignoring the input term. */
//    fun <A : Term> build(f: () -> A): Strategy<A> = Strategy { _ -> f() }
//
//    /** Maps a function over a value. */
//    fun <B : Term> map(f: (Term) -> B?): Strategy<B> = Strategy { t -> f(t) }
//
//    /** Succeeds only if the filter matches. */
//    fun filter(f: (Term) -> Boolean): Strategy<Term> = Strategy { t -> t.takeIf { f(it) } }
//
//    /** Succeeds only if the strategy succeeds. */
//    fun <B : Term> where(f: Strategy<B>): Strategy<Term> = Strategy { t -> t?.takeIf { f.apply(it) != null } }
//
//    /** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
//    fun <B : Term> glc(condition: Strategy<B>, onSuccess: Strategy<B>, onFail: Strategy<B>): Strategy<B> =
//        Strategy { t -> t?.let { condition.apply(it)?.let { it2 -> onSuccess.apply(it2) } ?: onFail.apply(it) } }
//
//    /** Succeeds only if the term is an application term. */
//    fun isAppl(): Strategy<ApplTerm> = Strategy { t -> t as? ApplTerm }
//
//    /** Succeeds only if the term is an application term of the specified type. */
//    fun isAppl(type: ApplTermType): Strategy<ApplTerm> = Strategy { t -> (t as? ApplTerm)?.takeIf { it.type == type } }
//
//    /** Succeeds only if the term is an integer term. */
//    fun isInt(): Strategy<IntTerm> = Strategy { t -> t as? IntTerm }
//
//    /** Succeeds only if the term is a string term. */
//    fun isString(): Strategy<StringTerm> = Strategy { t -> t as? StringTerm }
//
//    /** Succeeds only if the term is a blob term. */
//    fun isBlob(): Strategy<BlobTerm> = Strategy { t -> t as? BlobTerm }
//
//    /** Succeeds only if the term is a list term. */
//    fun isList(): Strategy<ListTerm> = Strategy { t -> t as? ListTerm }
//
//    /** Succeeds only if the term is a list term of the specified type. */
//    fun isList(type: ListTermType): Strategy<ListTerm> = Strategy { t -> (t as? ListTerm)?.takeIf { it.type == type } }
//
//    /** Succeeds only if the term is a var term. */
//    fun isVar(): Strategy<TermVar> = Strategy { t -> t as? TermVar }
//
//    /** Succeeds only if the term is a var term of the specified type. */
//    fun isVar(type: TermType): Strategy<TermVar> = Strategy { t -> (t as? TermVar)?.takeIf { it.type == type } }
//
//// Continuations
//
//    /** Identity. */
//    fun <A : Term> Strategy<A>.id(): Strategy<A> = this
//
//    /** Always fails. */
//    fun Strategy<Term>.fail(): Strategy<Nothing> = Strategy { null }
//
//    /** Succeeds only if the term matches the specified term. */ // TODO: Patterns?
//    fun <A : Term> Strategy<Term>.match(other: A): Strategy<A> =
//        Strategy { t -> apply(t)?.takeIf { it.matches(other) } as A? }
//
//    /** Builds a term, ignoring the input term. */
//    fun <A : Term> Strategy<Term>.build(f: () -> A): Strategy<A> = Strategy { _ -> f() }
//
//    /** Maps a function over a value. */
//    fun <A : Term, B : Term> Strategy<A>.map(f: (A) -> B?): Strategy<B> = Strategy { t -> apply(t)?.let { f(it) } }
//
//    /** Succeeds only if the filter matches. */
//    fun <A : Term> Strategy<A>.filter(f: (A) -> Boolean): Strategy<A> = Strategy { t -> apply(t)?.takeIf { f(it) } }
//
//    /** Succeeds only if the strategy succeeds. */
//    fun <A : Term, B : Term> Strategy<A>.where(f: Strategy<B>): Strategy<A> =
//        Strategy { t -> apply(t)?.takeIf { f.apply(it) != null } }
//
//    /** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
//    fun <A : Term, B : Term> Strategy<A>.glc(
//        condition: Strategy<B>,
//        onSuccess: Strategy<B>,
//        onFail: Strategy<B>
//    ): Strategy<B> =
//        Strategy { t -> apply(t)?.let { condition.apply(it)?.let { it2 -> onSuccess.apply(it2) } ?: onFail.apply(it) } }
//
//    /** Succeeds only if the term is an application term. */
//    fun Strategy<Term>.isAppl(): Strategy<ApplTerm> = Strategy { t -> apply(t) as? ApplTerm }
//
//    /** Succeeds only if the term is an application term of the specified type. */
//    fun Strategy<Term>.isAppl(type: ApplTermType): Strategy<ApplTerm> =
//        Strategy { t -> (apply(t) as? ApplTerm)?.takeIf { it.type == type } }
//
/////** Succeeds only if the term is an application term. */
////fun Strategy<ApplTerm>.isAppl(): Strategy<ApplTerm> = this
/////** Succeeds only if the term is an application term of the specified type. */
////fun Strategy<ApplTerm>.isAppl(type: ApplTermType): Strategy<ApplTerm> = Strategy { t -> apply(t)?.takeIf { it.type == type } }
//
//    /** Succeeds only if the term is an integer term. */
//    fun Strategy<Term>.isInt(): Strategy<IntTerm> = Strategy { t -> apply(t) as? IntTerm }
/////** Succeeds only if the term is an integer term. */
////fun Strategy<IntTerm>.isInt(): Strategy<IntTerm> = this
//
//    /** Succeeds only if the term is a string term. */
//    fun Strategy<Term>.isString(): Strategy<StringTerm> = Strategy { t -> apply(t) as? StringTerm }
/////** Succeeds only if the term is a string term. */
////fun Strategy<StringTerm>.isString(): Strategy<StringTerm> = this
//
//    /** Succeeds only if the term is a blob term. */
//    fun Strategy<Term>.isBlob(): Strategy<BlobTerm> = Strategy { t -> apply(t) as? BlobTerm }
/////** Succeeds only if the term is a blob term. */
////fun Strategy<BlobTerm>.isBlob(): Strategy<BlobTerm> = this
//
//    /** Succeeds only if the term is a list term. */
//    fun Strategy<Term>.isList(): Strategy<ListTerm> = Strategy { t -> apply(t) as? ListTerm }
//
//    /** Succeeds only if the term is a list term of the specified type. */
//    fun Strategy<Term>.isList(type: ListTermType): Strategy<ListTerm> =
//        Strategy { t -> (apply(t) as? ListTerm)?.takeIf { it.type == type } }
//
/////** Succeeds only if the term is a list term. */
////fun Strategy<ListTerm>.isList(): Strategy<ListTerm> = this
/////** Succeeds only if the term is a list term of the specified type. */
////fun Strategy<ListTerm>.isList(type: ListTermType): Strategy<ListTerm> = Strategy { t -> apply(t)?.takeIf { it.type == type } }
//
//    /** Succeeds only if the term is a var term. */
//    fun Strategy<Term>.isVar(): Strategy<TermVar> = Strategy { t -> apply(t) as? TermVar }
//
//    /** Succeeds only if the term is a var term of the specified type. */
//    fun Strategy<Term>.isVar(type: TermType): Strategy<TermVar> =
//        Strategy { t -> (apply(t) as? TermVar)?.takeIf { it.type == type } }
//
/////** Succeeds only if the term is a var term. */
////fun Strategy<TermVar>.isVar(): Strategy<TermVar> = this
/////** Succeeds only if the term is a var term of the specified type. */
////fun Strategy<TermVar>.isVar(type: TermType): Strategy<TermVar> = Strategy { t -> apply(t)?.takeIf { it.type == type } }
//
//// Terminals
//
//    fun main() {
//        val builder = DefaultTermBuilder()
//        val x = build { builder.createInt(10) }.glc(
//            build { builder.createInt(20) },
//            build { builder.createInt(30) },
//            build { builder.createInt(40) }
//        )
//
//        println(x.apply(builder.createInt(10)))
//    }
//}