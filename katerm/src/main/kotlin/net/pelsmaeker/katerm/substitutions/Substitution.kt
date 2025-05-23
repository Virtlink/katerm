package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermBuilder
import net.pelsmaeker.katerm.TermVar

/**
 * A substitution is a mapping of variables to terms.
 */
interface Substitution {

    /**
     * Determines whether the substitution is empty.
     *
     * @return `true` if the substitution is empty; otherwise, `false`.
     */
    fun isEmpty(): Boolean

    /**
     * Determines whether the substitution is not empty.
     *
     * @return `true` if the substitution is not empty; otherwise, `false`.
     */
    fun isNotEmpty(): Boolean = !isEmpty()

    /**
     * The set of variables in the substitution.
     */
    val variables: Set<TermVar>

    /**
     * Gets the term that the given variable is mapped to.
     *
     * @param variable The variable to look up.
     * @return The term the variable is mapped to, or the variable itself if the variable is not in the substitution.
     */
    operator fun get(variable: TermVar): Term

    /**
     * Find the representative variable for the given variable.
     *
     * @param variable The variable to find the representative for.
     * @return The representative variable; or `null` when the variable is not in the substitution.
     */
    fun find(variable: TermVar): TermVar?

    /**
     * Checks whether the substitution contains the given variable.
     *
     * @param variable The variable to check.
     * @return `true` if the variable is in the substitution; otherwise, `false`.
     */
    operator fun contains(variable: TermVar): Boolean =
        find(variable) != null

    /**
     * Checks whether the given term is equal to another term.
     *
     * This method checks whether the two terms are equal relative to this substitution.
     *
     * @param left The first term to check.
     * @param right The second term to check.
     * @return `true` if the terms are equal relative to this substitution; otherwise, `false`.
     */
    fun areEqual(left: Term, right: Term): Boolean

    /**
     * Gets the set of free variables in the given term relative to this substitution.
     *
     * This method returns the set of variables that are not fully instantiated
     * by this substitution.
     *
     * @param term The term to check.
     * @return The set of variables in the term.
     */
    fun getFreeVars(term: Term): Set<TermVar>

    /**
     * Checks whether the given term is a ground term relative to this substitution.
     *
     * A ground term is a term that contains no variables.
     *
     * @param term The term to check.
     * @return `true` if the term is a ground term relative to this substitution; otherwise, `false`.
     */
    fun isGround(term: Term): Boolean

    /**
     * Transforms the substitution into a map of variable sets to terms.
     *
     * @return A map where the keys are sets of variables and the values are terms.
     */
    fun toMap(): Map<Set<TermVar>, Term>

    /**
     * Applies the substitution to the given term.
     *
     * @param term The term to apply the substitution to.
     * @param termBuilder The term builder to use for creating new terms.
     * @return The term with the substitution applied.
     */
    fun apply(term: Term, termBuilder: TermBuilder): Term =
        termBuilder.apply(term, this)

}