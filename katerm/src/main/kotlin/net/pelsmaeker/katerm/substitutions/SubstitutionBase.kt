package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.ApplTerm
import net.pelsmaeker.katerm.ListTerm
import net.pelsmaeker.katerm.OptionTerm
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

/**
 * Base class for implementations of [Substitution].
 */
abstract class SubstitutionBase : Substitution {

    override fun areEqual(left: Term, right: Term): Boolean {
        return this.unify(left, right) != null
    }

    override fun getFreeVars(term: Term): Set<TermVar> {
        return when (term) {
            is TermVar -> {
                val mappedTerm = this[term]
                if (mappedTerm !is TermVar) {
                    // Also gather from the mapped term.
                    getFreeVars(mappedTerm)
                } else setOf(term)
            }
            is ApplTerm -> term.termArgs.flatMapTo(HashSet()) { getFreeVars(it) }
            is OptionTerm<*> -> {
                if (term.variable != null) { TODO("Variables in OptionTerm are not yet supported.") }
                term.element?.let { getFreeVars(it) } ?: emptySet()
            }
            is ListTerm<*> -> {
                if (term.prefix != null) { TODO("Variables in ListTerm are not yet supported.") }
                term.elements.flatMapTo(HashSet()) { getFreeVars(it) }
            }
            else -> emptySet()
        }
    }

    override fun isGround(term: Term): Boolean = when (term) {
        is TermVar -> {
            val mappedTerm = this[term]
            if (mappedTerm !is TermVar) {
                // Also check the mapped term.
                isGround(mappedTerm)
            } else false
        }
        is ApplTerm -> term.termArgs.all { isGround(it) }
        is OptionTerm<*> -> {
            if (term.variable != null) { TODO("Variables in OptionTerm are not yet supported.") }
            term.element?.let { isGround(it) } ?: true
        }
        is ListTerm<*> -> {
            if (term.prefix != null) { TODO("Variables in ListTerm are not yet supported.") }
            term.elements.all { isGround(it) }
        }
        else -> true
    }
}