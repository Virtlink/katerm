package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

class MutableUnionFindSubstitution(
    private val roots: MutableMap<TermVar, Term>,
    private val parents: MutableMap<TermVar, TermVar>,             // We replace this map when doing path compression.
    private val ranks: MutableMap<TermVar, Int>,                   // We replace this map when doing path compression.
): SubstitutionBase(), MutableSubstitution {

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override val variables: Set<TermVar>
        get() = TODO("Not yet implemented")

    override fun get(variable: TermVar): Term {
        TODO("Not yet implemented")
    }

    override fun find(variable: TermVar): TermVar? {
        TODO("Not yet implemented")
    }

    override fun set(
        variable: TermVar,
        term: Term,
    ): Term? {
        TODO("Not yet implemented")
    }

    override fun remove(variable: TermVar): Term? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}