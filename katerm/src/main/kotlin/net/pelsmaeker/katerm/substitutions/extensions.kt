package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermVar

/**
 * Returns a new empty substitution.
 *
 * @return An empty substitution.
 */
fun emptySubstitution(): ImmutableSubstitution = TODO()

/**
 * Returns a new substitution from the given pairs.
 *
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new substitution with the given pairs.
 */
fun substitutionOf(vararg pairs: Pair<TermVar, Term>): ImmutableSubstitution {
    return TODO()
}

/**
 * Returns a new mutable substitution from the given pairs.
 *
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new mutable substitution with the given pairs.
 */
fun mutableSubstitutionOf(vararg pairs: Pair<TermVar, Term>): MutableSubstitution {
    return TODO()
}

/**
 * Returns a new persistent substitution from the given pairs.
 *
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new persistent substitution with the given pairs.
 */
fun persistentSubstitutionOf(vararg pairs: Pair<TermVar, Term>): PersistentSubstitution {
    return TODO()
}

/**
 * Converts a read-only or mutable substitution to an immutable one.
 * If the receiver is already immutable, it is returned as-is.
 *
 * @receiver The substitution for which to return an immutable version.
 * @return The immutable substitution.
 */
fun Substitution.toImmutableSubstititution(): ImmutableSubstitution {
    if (this is ImmutableSubstitution) return this
    return TODO()
}

/**
 * Converts a read-only or mutable substitution to a persistent one.
 * If the receiver is already persistent, it is returned as-is.
 *
 * @receiver The substitution for which to return a persistent version.
 * @return The persistent substitution.
 */
fun Substitution.toPersistentSubstitution(): PersistentSubstitution {
    if (this is PersistentSubstitution) return this
    return TODO()
}

/**
 * Returns a new mutable substitution with the same contents as the receiver.
 *
 * Modifying the returned mutable substitution does not modify the original substitution,
 * even if it was already mutable.
 *
 * @receiver The substitution for which to return a mutable version.
 * @return The mutable substitution.
 */
fun Substitution.toMutableSubstitution(): MutableSubstitution {
    return TODO()
}
