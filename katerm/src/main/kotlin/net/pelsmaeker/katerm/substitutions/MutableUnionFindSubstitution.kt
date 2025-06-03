package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermBuilder
import net.pelsmaeker.katerm.terms.TermVar
import net.pelsmaeker.katerm.collections.MutableDisjointMap
import net.pelsmaeker.katerm.collections.MutableUnionFindMapImpl
import java.util.LinkedHashSet

/**
 * A mutable substitution that uses a union-find structure to manage variable mappings.
 *
 * @property termBuilder The term builder used to create terms.
 */
class MutableUnionFindSubstitution(
    override val termBuilder: TermBuilder,
): SubstitutionBase(), MutableSubstitution {

    private val disjointMap: MutableDisjointMap<TermVar, Term> = MutableUnionFindMapImpl()

    override fun isEmpty(): Boolean = disjointMap.isEmpty()

    override val termVars: Set<TermVar> get() = disjointMap.elements

    override fun get(variable: TermVar, instantiate: Boolean): Term {
        val term = disjointMap.getOrDefault(variable, variable)
        return if (instantiate) apply(term) else term
    }

    override fun find(variable: TermVar): TermVar? {
        return disjointMap.find(variable)
    }

    override fun set(
        variable: TermVar,
        term: Term,
    ): Term? {
        return disjointMap.set(variable, term)
    }

    override fun clear() {
        disjointMap.clear()
    }

    override fun toMap(instantiate: Boolean): Map<Set<TermVar>, Term> {
        val sets = mutableMapOf<TermVar, MutableSet<TermVar>>()
        for (element in disjointMap.elements) {
            val representative = find(element) ?: continue
            sets.getOrPut(representative) { LinkedHashSet(setOf(representative)) }.add(element)
        }
        return sets.map { (repr, set) -> set to get(repr, instantiate) }.toMap()
    }

    override fun toString(): String {
        return disjointMap.toString()
    }

}