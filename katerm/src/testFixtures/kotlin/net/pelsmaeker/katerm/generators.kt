package net.pelsmaeker.katerm

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.resolution.GlobalArbResolver
import net.pelsmaeker.katerm.regex.RegexNfaBuilder
import net.pelsmaeker.katerm.regex.TermRegexBuilder
import net.pelsmaeker.katerm.terms.ApplTerm
import net.pelsmaeker.katerm.terms.ConcatListTerm
import net.pelsmaeker.katerm.terms.ConsListTerm
import net.pelsmaeker.katerm.terms.SimpleTermBuilder
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar
import net.pelsmaeker.katerm.terms.IntTerm
import net.pelsmaeker.katerm.terms.ListTerm
import net.pelsmaeker.katerm.terms.OptionTerm
import net.pelsmaeker.katerm.terms.RealTerm
import net.pelsmaeker.katerm.terms.SomeOptionTerm
import net.pelsmaeker.katerm.terms.StringTerm
import net.pelsmaeker.katerm.terms.TermBuilder
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.reflect.typeOf

/** The term builder used in tests. */
val testTermBuilder = SimpleTermBuilder()

/** The regex builder used in tests. */
val testRegexBuilder = TermRegexBuilder(
    termBuilder = testTermBuilder,
    regexBuilder = RegexNfaBuilder(),
)

/** A generator for arbitrary [TermVar] instances. */
fun Arb.Companion.termVar(): Arb<TermVar> = arbitrary(
    edgecaseFn = { rs: RandomSource ->
        val name = Arb.string().edgecase(rs) ?: "x"
        testTermBuilder.newVar(name)
    },
    sampleFn = { rs: RandomSource ->
        val name = Arb.string().next(rs)
        testTermBuilder.newVar(name)
    }
)

/** A generator for arbitrary [IntTerm] instances. */
fun Arb.Companion.intTerm(): Arb<IntTerm> = arbitrary(
    edgecaseFn = { rs: RandomSource ->
        val value = Arb.int().edgecase(rs) ?: 0
        testTermBuilder.newInt(value)
    },
    sampleFn = { rs: RandomSource ->
        val value = Arb.int().next(rs)
        testTermBuilder.newInt(value)
    }
)

/** A generator for arbitrary [RealTerm] instances. */
fun Arb.Companion.realTerm(): Arb<RealTerm> = arbitrary(
    edgecaseFn = { rs: RandomSource ->
        val value = Arb.double().edgecase(rs) ?: 0.0
        testTermBuilder.newReal(value)
    },
    sampleFn = { rs: RandomSource ->
        val value = Arb.double().next(rs)
        testTermBuilder.newReal(value)
    }
)

/** A generator for arbitrary [StringTerm] instances. */
fun Arb.Companion.stringTerm(): Arb<StringTerm> = arbitrary(
    edgecaseFn = { rs: RandomSource ->
        val value = Arb.string().edgecase(rs) ?: "Foo"
        testTermBuilder.newString(value)
    },
    sampleFn = { rs: RandomSource ->
        val value = Arb.string().next(rs)
        testTermBuilder.newString(value)
    }
)

/**
 * A generator for arbitrary [ApplTerm] instances.
 *
 * @param maxDepth The maximum depth of the term tree.
 */
fun Arb.Companion.applTerm(maxDepth: Int = 3): Arb<ApplTerm> {
    require(maxDepth >= 0) { "maxDepth must be positive or zero; got: $maxDepth" }

    return arbitrary(
        edgecaseFn = { rs: RandomSource ->
            val op = Arb.string().edgecase(rs) ?: "Appl"
            val args = if (maxDepth > 0) Arb.list(Arb.term(maxDepth - 1), 0..3).edgecase(rs) ?: emptyList() else emptyList()
            testTermBuilder.newAppl(op, args)
        },
        sampleFn = { rs: RandomSource ->
            val op = Arb.string().next(rs)
            val args = if (maxDepth > 0) Arb.list(Arb.term(maxDepth - 1), 0..3).next(rs) else emptyList()
            testTermBuilder.newAppl(op, args)
        }
    )
}


/**
 * A generator for arbitrary [OptionTerm] instances.
 *
 * @param maxDepth The maximum depth of the term tree.
 */
fun Arb.Companion.optionTerm(maxDepth: Int = 3): Arb<OptionTerm<Term>> {
    require(maxDepth >= 0) { "maxDepth must be positive or zero; got: $maxDepth" }

    return arbitrary(
        edgecaseFn = { rs: RandomSource ->
            val element = if (maxDepth > 0) Arb.term(maxDepth - 1).orNull().edgecase(rs) else null
            testTermBuilder.newOption(element)
        },
        sampleFn = { rs: RandomSource ->
            val element = if (maxDepth > 0) Arb.term(maxDepth - 1).orNull().next(rs) else null
            testTermBuilder.newOption(element)
        }
    )
}

/**
 * A generator for arbitrary [ListTerm] instances.
 *
 * @param maxDepth The maximum depth of the term tree.
 */
fun Arb.Companion.listTerm(maxDepth: Int = 3): Arb<ListTerm<Term>> {
    require(maxDepth >= 0) { "maxDepth must be positive or zero; got: $maxDepth" }

    return arbitrary(
        edgecaseFn = { rs: RandomSource ->
            val elements = if (maxDepth > 0) Arb.list(Arb.term(maxDepth - 1), 0..3).edgecase(rs) ?: emptyList() else emptyList()
            testTermBuilder.newListOf(elements)
        },
        sampleFn = { rs: RandomSource ->
            val elements = if (maxDepth > 0) Arb.list(Arb.term(maxDepth - 1), 0..3).next(rs) else emptyList()
            testTermBuilder.newListOf(elements)
        }
    )
}

/**
 * A generator for arbitrary [Term] instances.
 *
 * @param maxDepth The maximum depth of the term tree.
 */
fun Arb.Companion.term(maxDepth: Int): Arb<Term> {
    require(maxDepth >= 0) { "maxDepth must be positive or zero; got: $maxDepth" }

    return Arb.choice(
        Arb.termVar(),
        Arb.intTerm(),
        Arb.realTerm(),
        Arb.stringTerm(),
        Arb.applTerm(maxDepth),
        Arb.optionTerm(maxDepth),
        Arb.listTerm(maxDepth),
    )
}


/**
 * Registers the term generators with the global Arb resolver.
 */
fun registerTermArbs() {
    val maxDepth = 3
    val arbs = listOf(
        typeOf<Term>() to Arb.term(maxDepth),
        typeOf<TermVar>() to Arb.termVar(),
        typeOf<IntTerm>() to Arb.intTerm(),
        typeOf<RealTerm>() to Arb.realTerm(),
        typeOf<StringTerm>() to Arb.stringTerm(),
        typeOf<ApplTerm>() to Arb.applTerm(maxDepth),
        typeOf<OptionTerm<*>>() to Arb.optionTerm(maxDepth),
        typeOf<ListTerm<*>>() to Arb.listTerm(maxDepth),
    )
    arbs.forEach { (t, a) ->
        GlobalArbResolver.register(t, a)
    }
}

/**
 * Adds random holes to the given term.
 *
 * @receiver The term builder.
 * @param term The term to add holes to.
 * @param chance The chance of adding a hole to a term.
 * @param random The random number generator.
 * @return A pair of the term with holes and a map of the holes.
 */
fun TermBuilder.addHolesToTerm(term: Term, chance: Double = 0.2, random: Random = Random): Pair<Term, Map<TermVar, Term>> {
    require(chance in 0.0..1.0) { "Chance must be between 0.0 and 1.0; got: $chance" }

    val counter = AtomicInteger(0)
    val holeMap = mutableMapOf<TermVar, Term>()
    val newTerm = addHolesToTerm(term, chance, random, { newVar("hole${counter.getAndIncrement()}") }, holeMap)
    return newTerm to holeMap
}

/**
 * Adds random holes to the given term.
 *
 * @receiver The term builder.
 * @param term The term to add holes to.
 * @param freshVarProvider A function that provides fresh variables.
 * @param holeMap A mutable map to keep track of the holes.
 * @param chance The chance of adding a hole to a term.
 * @param random The random number generator.
 * @return The term with holes.
 */
private fun TermBuilder.addHolesToTerm(term: Term, chance: Double, random: Random, freshVarProvider: () -> TermVar, holeMap: MutableMap<TermVar, Term>): Term {
    require(chance in 0.0..1.0) { "Chance must be between 0.0 and 1.0; got: $chance" }

    if (random.nextDouble() < chance) {
        // Make a hole
        val hole = freshVarProvider()
        holeMap[hole] = term
        return hole
    } else {
        // Recursively add holes to the term
        @Suppress("UNCHECKED_CAST")
        return when (term) {
            is ApplTerm -> {
                val newArgs = term.termChildren.map { arg -> addHolesToTerm(arg, chance, random, freshVarProvider, holeMap) }
                newAppl(term.termOp, newArgs)
            }
            is ConsListTerm<*> -> {
                val newHead = addHolesToTerm(term.head, chance, random, freshVarProvider, holeMap)
                val newTail = addHolesToTerm(term.tail, chance, random, freshVarProvider, holeMap) as ListTerm<*>
                newList(newHead, newTail)
            }
            is ConcatListTerm<*> -> {
                val newLeft = addHolesToTerm(term.left, chance, random, freshVarProvider, holeMap) as ListTerm<*>
                val newRight = addHolesToTerm(term.right, chance, random, freshVarProvider, holeMap) as ListTerm<*>
                concatLists(newLeft, newRight)
            }
            is SomeOptionTerm<*> -> {
                val newElement = addHolesToTerm(term.element, chance, random, freshVarProvider, holeMap)
                newOption(newElement)
            }
            else -> term
        }
    }
}