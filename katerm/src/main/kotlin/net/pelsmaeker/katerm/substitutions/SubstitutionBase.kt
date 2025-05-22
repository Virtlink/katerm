package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.ApplTerm
import net.pelsmaeker.katerm.ConcatListTerm
import net.pelsmaeker.katerm.ConsListTerm
import net.pelsmaeker.katerm.ListTerm
import net.pelsmaeker.katerm.OptionTerm
import net.pelsmaeker.katerm.SomeOptionTerm
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

/**
 * Base class for implementations of [Substitution].
 */
abstract class SubstitutionBase : Substitution {

    override fun areEqual(left: Term, right: Term): Boolean {
        return this.unify(left, right) != null
    }

    override fun getFreeVars(term: Term): Set<TermVar> = when (term) {
        is TermVar -> {
            val mappedTerm = this[term]
            if (mappedTerm !is TermVar) {
                // Also gather from the mapped term.
                getFreeVars(mappedTerm)
            } else setOf(term)
        }
        is ApplTerm -> term.termArgs.flatMapTo(HashSet()) { getFreeVars(it) }
        is SomeOptionTerm<*> -> getFreeVars(term.element)
        is ConsListTerm<*> -> getFreeVars(term.head) + getFreeVars(term.tail)
        is ConcatListTerm<*> -> getFreeVars(term.left) + getFreeVars(term.right)
        else -> emptySet()
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
        is SomeOptionTerm<*> -> isGround(term.element)
        is ConsListTerm<*> -> isGround(term.head) && isGround(term.tail)
        is ConcatListTerm<*> -> isGround(term.left) && isGround(term.right)
        else -> true
    }
}