package net.pelsmaeker.katerm

/**
 * Transforms.
 */
fun interface Strategy<in I, out O> {
    /**
     * Applies the strategy to the specified value
     *
     * @return the result of the strategy; or `null` if the value did not match
     */
    fun apply(value: I): O?
}

// Initials

/** Identity. */
fun <A: Any> id(): Strategy<A, A> = Strategy { t -> t }
/** Always fails. */
fun <A: Any> fail(): Strategy<A, Nothing> = id<A>().fail()
/** Builds a term, ignoring the input term. */
fun <A: Any, B: Any> build(f: () -> B): Strategy<A, B> = id<A>().build(f)

/** Maps a function over a value. */
fun <A: Any, B: Any> map(f: (A) -> B?): Strategy<A, B> = id<A>().map(f)
/** Succeeds only if the filter matches. */
fun <A: Any> filter(f: (A) -> Boolean): Strategy<A, A> = id<A>().filter(f)
/** Succeeds only if the strategy succeeds. */
fun <A: Any> where(f: Strategy<A, Any>): Strategy<A, A> = id<A>().where(f)
/** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
fun <A: Any, B: Any, C: Any> glc(condition: Strategy<A, B>, onSuccess: Strategy<B, C>, onFail: Strategy<A, C>): Strategy<A, C> = id<A>().glc(condition, onSuccess, onFail)

// Compositions

/** Identity. */
fun <A: Any, B: Any> Strategy<A, B>.id(): Strategy<A, B> = this
/** Always fails. */
fun <A: Any> Strategy<A, Any>.fail(): Strategy<A, Nothing> = Strategy { null }
/** Builds a term, ignoring the input term. */
fun <A: Any, B: Any, C: Any> Strategy<A, B>.build(f: () -> C): Strategy<A, C> = Strategy { _ -> f() }

/** Maps a function over a value. */
fun <A: Any, B: Any, C: Any> Strategy<A, B>.map(f: (B) -> C?): Strategy<A, C> = Strategy { t -> apply(t)?.let { f(it) } }
/** Succeeds only if the filter matches. */
fun <A: Any, B: Any> Strategy<A, B>.filter(f: (B) -> Boolean): Strategy<A, B> = Strategy { t -> apply(t)?.takeIf { f(it) } }
/** Succeeds only if the strategy succeeds. */
fun <A: Any, B: Any, C: Any> Strategy<A, B>.where(f: Strategy<B, C>): Strategy<A, B> = Strategy { t -> apply(t)?.takeIf { f.apply(it) != null } }
/** Guarded-left-choice applies the first strategy, and if it succeeds the second, otherwise the third on the original term. */
fun <A: Any, B: Any, C: Any, D: Any> Strategy<A, B>.glc(condition: Strategy<B, C>, onSuccess: Strategy<C, D>, onFail: Strategy<B, D>): Strategy<A, D> = Strategy { t -> apply(t)?.let { condition.apply(it)?.let { it2 -> onSuccess.apply(it2) } ?: onFail.apply(it) } }

/** Succeeds only if the term is an integer term. */
fun <A: Any> Strategy<A, Any>.isInt(): Strategy<A, Int> = Strategy { t -> apply(t) as? Int }
/** Succeeds only if the term is an integer term. */
@JvmName("isInt2") fun <A: Any> Strategy<A, Int>.isInt(): Strategy<A, Int> = this // Optimization, requires JvmName

/** Repeatedly (0 or more times) applies strategy s until it fails. */
fun <A: Any, B: Any> Strategy<A, B>.repeat(s: Strategy<B, B>): Strategy<A, B> = Strategy { t -> apply(t)?.let { id<B>().repeat(s).apply(it) } }

fun <A: Any, B: Any> Strategy<A, List<List<B>>>.flatten(): Strategy<A, List<B>> = Strategy { t -> apply(t)?.flatten() }

fun <A: Any, B: Any, C: Any> Strategy<A, B>.ntl(s: Strategy<B, C>): Strategy<A, List<C>> = Strategy { t -> apply(t)?.let { s.apply(it) }?.let { listOf(it) } ?: listOf() }

fun main() {
//    val builder = DefaultTermBuilder()
//    val x = build { builder.crseateInt(10) }.glc(
//        { t -> builder.createString(t.value.toString()) },
//        { t -> builder.createInt(t.value.toInt()) },
//        build { builder.createString("40") }
//    ).fail()
//
//    println(x.apply(builder.createInt(10)))
}