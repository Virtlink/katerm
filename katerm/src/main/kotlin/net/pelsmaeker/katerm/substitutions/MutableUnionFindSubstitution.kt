package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar
import net.pelsmaeker.katerm.collections.MutableDisjointMap
import net.pelsmaeker.katerm.collections.MutableUnionFindMapImpl

class MutableUnionFindSubstitution: SubstitutionBase(), MutableSubstitution {

    private val disjointMap: MutableDisjointMap<TermVar, Term> = MutableUnionFindMapImpl()

    override fun isEmpty(): Boolean = disjointMap.isEmpty()

    override val variables: Set<TermVar> get() = disjointMap.elements

    override fun get(variable: TermVar): Term {
        return disjointMap[variable] ?: variable
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

    override fun toMap(): Map<Set<TermVar>, Term> {
        return disjointMap.toMap()
    }

    override fun toString(): String {
        return disjointMap.toString()
    }

}