package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

/**
 * Unifies two terms, and returns a substitution that makes them equal.
 *
 * @param left The first term to unify.
 * @param right The second term to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun unify(left: Term, right: Term): Substitution? {
    return emptySubstitution().unify(left, right)
}

/**
 * Unifies a collection of pairs of terms,
 * and returns a substitution that makes each equal.
 *
 * @param pairs The pairs of terms to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun unifyAll(pairs: Iterable<Pair<Term, Term>>): Substitution? {
    return emptySubstitution().unifyAll(pairs)
}

/**
 * Unifies two terms with respect to the given substitution,
 * and returns a modified substitution that makes them equal.
 *
 * @receiver The substitution to use for unification.
 * @param left The first term to unify.
 * @param right The second term to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun Substitution.unify(left: Term, right: Term): Substitution? {
    val mutableSubstitution = this.toMutableSubstitution()
    val success = UnifyOperation(left, right, mutableSubstitution).unifyAll()
    return mutableSubstitution.takeIf { success }
}

/**
 * Unifies a collection of pairs of terms with respect to the given substitution,
 * and returns a modified substitution that makes each equal.
 *
 * @receiver The substitution to use for unification.
 * @param pairs The pairs of terms to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun Substitution.unifyAll(pairs: Iterable<Pair<Term, Term>>): Substitution? {
    val mutableSubstitution = this.toMutableSubstitution()
    val success = UnifyOperation(pairs, mutableSubstitution).unifyAll()
    return mutableSubstitution.takeIf { success }
}

/**
 * Performs a unification operation, mutating the specified substitution to make the terms equal.
 *
 * @property substitution The substitution to mutate. If unification fails, the substitution is in an invalid intermediate state.
 */
private class UnifyOperation(
    private val substitution: MutableSubstitution,
) {
    /** The queue of pairs of terms to unify. */
    private val worklist: ArrayDeque<Pair<Term, Term>> = ArrayDeque()

    constructor(left: Term, right: Term, substitution: MutableSubstitution) : this(substitution) {
        worklist.add(Pair(left, right))
    }

    constructor(pairs: Iterable<Pair<Term, Term>>, substitution: MutableSubstitution) : this(substitution) {
        worklist.addAll(pairs)
    }

    /**
     * Unifies all terms in the worklist.
     *
     * When this method returns `true`, the unification succeeded and the [substitution] was modified such that
     * it makes all terms in the worklist equal modulo attachments.
     *
     * When this method returns `false`, the unification failed and [substitution] is in an invalid intermediate state.
     *
     * @return `true` if the unification was successful; otherwise, `false`.
     */
    fun unifyAll(): Boolean {
        while (worklist.isNotEmpty()) {
            val (term1, term2) = worklist.removeFirst()

            when {
                // If the terms are already equal, continue.
                // This includes the cases where both terms are variables or literals and are equal.
                term1 == term2 -> continue

                // If term1 is a variable, substitute it
                term1 is TermVar -> {
                    val mappedTerm = substitution[term1]
                    if (mappedTerm !is TermVar) {
                        // Unify the mapped term with term2
                        worklist.add(Pair(mappedTerm, term2))
                    } else {
                        // Substitute variable term1 with term2
                        if (occursCheck(term2, term1)) return false
                        substitution[term1] = term2
                    }
                }

                // If term2 is a variable, substitute it
                term2 is TermVar -> {
                    val mappedTerm = substitution[term2]
                    if (mappedTerm !is TermVar) {
                        // Unify the mapped term with term1
                        worklist.add(Pair(mappedTerm, term1))
                    } else {
                        // Substitute variable term2 with term1
                        if (occursCheck(term1, term2)) return false
                        substitution[term2] = term1
                    }
                }

                // Otherwise, the terms must compare equal (modulo subterms) and we unify their subterms
                else -> {
                    if (!term1.equals(term2, compareSubterms = false, compareAttachments = false)) return false
                    worklist.addAll(term1.termChildren.zip(term2.termChildren))
                }
            }
        }

        return true
    }

    /**
     * Checks if the variable occurs in the term (occurs check).
     *
     * @param term The term to check against.
     * @param variable The variable to check.
     * @return `true` if the variable occurs in the term; otherwise, `false`.
     */
    private fun occursCheck(term: Term, variable: TermVar): Boolean {
        return variable == term || term.termVars.contains(variable)
    }

}