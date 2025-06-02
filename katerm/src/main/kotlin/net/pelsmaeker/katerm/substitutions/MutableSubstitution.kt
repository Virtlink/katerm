package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar

/**
 * A mutable substitution is a mapping of variables to terms that can be modified.
 */
interface MutableSubstitution : Substitution {

    /**
     * Adds or updates the mapping of the given variable to the specified term.
     *
     * @param variable The variable to map.
     * @param term The term to map the variable to.
     * @return The previous term mapped to the variable, or `null` if there was no previous mapping.
     */
    operator fun set(variable: TermVar, term: Term): Term?

    /**
     * Clears all mappings in the substitution.
     */
    fun clear()

}