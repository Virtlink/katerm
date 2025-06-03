package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermBuilder
import net.pelsmaeker.katerm.terms.TermVar

/**
 * Unifies two terms, and returns a substitution that makes them equal.
 *
 * @param left The first term to unify.
 * @param right The second term to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun TermBuilder.unify(left: Term, right: Term): Substitution? {
    return emptySubstitution().unify(left, right)
}

/**
 * Unifies a collection of pairs of terms,
 * and returns a substitution that makes each equal.
 *
 * @param pairs The pairs of terms to unify.
 * @return A substitution that makes the terms equal, or `null` if unification fails.
 */
fun TermBuilder.unifyAll(pairs: Iterable<Pair<Term, Term>>): Substitution? {
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
    return unifyAll(listOf(left to right))
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
    try {
        UnifyOperation(pairs, mutableSubstitution).unifyAll()
    } catch (_: UnificationError) {
        return null
    }
    return mutableSubstitution
}

/**
 * Unifies two substitutions.
 *
 * @receiver The first substitution to unify.
 * @param substitution The second substitution to unify.
 * @return The resulting substitution, or `null` if unification fails.
 */
fun Substitution.unifyWith(substitution: Substitution): Substitution? {
    return unifyAll(substitution.termVars.map { it to substitution[it] })
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
     * When this method throws, the unification succeeded and the [substitution] was modified such that
     * it makes all terms in the worklist equal modulo attachments.
     *
     * When this method throws, the unification failed and [substitution] is in an invalid intermediate state.
     *
     * @throws UnificationError If the unification fails.
     */
    fun unifyAll() {
        while (worklist.isNotEmpty()) {
            val (term1, term2) = worklist.removeFirst()

            try {
                when {
                    // If the terms are already equal, continue.
                    // This includes the cases where both terms are variables or literals and are equal.
                    term1 == term2 -> continue

                    // If term1 is a variable: ?x
                    term1 is TermVar -> {
                        val mappedTerm = substitution[term1]
                        // ?x |-> x'
                        if (mappedTerm is TermVar) {
                            // Substitute variable term1 with term2
                            occursCheck(term2, term1)
                            substitution[term1] = term2
                        } else {
                            // Unify the mapped term with term2: unify (x', term2)
                            worklist.add(Pair(mappedTerm, term2))
                        }
                    }

                    // If term2 is a variable ?y
                    term2 is TermVar -> {
                        val mappedTerm = substitution[term2]
                        if (mappedTerm is TermVar) {
                            // Substitute variable term2 with term1
                            occursCheck(term1, term2)
                            substitution[term2] = term1
                        } else {
                            // Unify the mapped term with term1
                            worklist.add(Pair(mappedTerm, term1))
                        }
                    }

                    // Otherwise, the terms must compare equal (modulo subterms) and we unify their subterms
                    else -> {
                        check(term1.equals(term2, compareSubterms = false, compareAttachments = false)) { "Terms are not equal (modulo subterms and attachments)." }

                        worklist.addAll(term1.termChildren.zip(term2.termChildren))
                    }
                }
            } catch (e: Throwable) {
                throw UnificationError(term1, term2, e.message ?: "Unspecified error of type ${e::class.simpleName}")
            }
        }
    }

    /**
     * Checks that the variable doesn't occur in the term (occurs check).
     *
     * @param term The term to check against.
     * @param variable The variable to check
     * @throws IllegalStateException If the variable occurs in the term.
     */
    private fun occursCheck(term: Term, variable: TermVar) {
        val repr = substitution.find(variable) ?: variable
        val occurrence = term.termVars.firstOrNull { (substitution.find(it) ?: it) == repr }
        if(occurrence != null) throw OccursCheckFailedException(variable, occurrence, repr, term)
    }

}
