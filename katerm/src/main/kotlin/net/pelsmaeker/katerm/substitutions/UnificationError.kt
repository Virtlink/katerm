package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term

/**
 * A unification error.
 *
 * @property term1 The first term involved in the unification error.
 * @property term2 The second term involved in the unification error.
 * @param message A description of the error message.
 */
class UnificationError(
    val term1: Term,
    val term2: Term,
    message: String,
) : Exception("Unification error: $message\nTerm 1: $term1\nTerm 2: $term2")