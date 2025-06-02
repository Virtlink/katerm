package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermBuilder
import net.pelsmaeker.katerm.TermBuilderHelper
import net.pelsmaeker.katerm.TermVar

/**
 * Returns a new empty substitution.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @return An empty substitution.
 */
fun TermBuilder.emptySubstitution(): ImmutableSubstitution = EmptySubstitution(this)


/**
 * Returns a new substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new substitution with the given pairs.
 */
fun TermBuilder.substitutionOf(vararg pairs: Pair<TermVar, Term>): ImmutableSubstitution {
    return substitutionOf(pairs.asList())
}

/**
 * Returns a new mutable substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new mutable substitution with the given pairs.
 */
fun TermBuilder.mutableSubstitutionOf(vararg pairs: Pair<TermVar, Term>): MutableSubstitution {
    return mutableSubstitutionOf(pairs.asList())
}

/**
 * Returns a new persistent substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new persistent substitution with the given pairs.
 */
fun TermBuilder.persistentSubstitutionOf(vararg pairs: Pair<TermVar, Term>): PersistentSubstitution {
    return persistentSubstitutionOf(pairs.asList())
}


/**
 * Returns a new substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new substitution with the given pairs.
 */
fun TermBuilder.substitutionOf(pairs: Collection<Pair<TermVar, Term>>): ImmutableSubstitution {
    if (pairs.isEmpty()) return emptySubstitution()
    if (pairs.size == 1) return pairs.first().let { first -> SingletonSubstitution(this, first.first, first.second) }
    return TODO()
}

/**
 * Returns a new mutable substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new mutable substitution with the given pairs.
 */
fun TermBuilder.mutableSubstitutionOf(pairs: Collection<Pair<TermVar, Term>>): MutableSubstitution {
    // TODO: This can be more optimized
    return substitutionOf(pairs).toMutableSubstitution()
}

/**
 * Returns a new persistent substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param pairs The pairs of variables and terms to include in the substitution.
 * @return A new persistent substitution with the given pairs.
 */
fun TermBuilder.persistentSubstitutionOf(pairs: Collection<Pair<TermVar, Term>>): PersistentSubstitution {
    return substitutionOf(pairs).toPersistentSubstitution()
}


/**
 * Returns a new substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param mapping The mapping of variables and terms to include in the substitution.
 * @return A new substitution with the given pairs.
 */
fun TermBuilder.substitutionOf(mapping: Map<TermVar, Term>): ImmutableSubstitution {
    return substitutionOf(mapping.entries.map { (a, b) -> a to b })
}

/**
 * Returns a new mutable substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param mapping The mapping of variables and terms to include in the substitution.
 * @return A new mutable substitution with the given pairs.
 */
fun TermBuilder.mutableSubstitutionOf(mapping: Map<TermVar, Term>): MutableSubstitution {
    return mutableSubstitutionOf(mapping.entries.map { (a, b) -> a to b })
}

/**
 * Returns a new persistent substitution from the given pairs.
 *
 * @receiver The term builder to use for creating/instantiating terms.
 * @param mapping The mapping of variables and terms to include in the substitution.
 * @return A new persistent substitution with the given pairs.
 */
fun TermBuilder.persistentSubstitutionOf(mapping: Map<TermVar, Term>): PersistentSubstitution {
    return persistentSubstitutionOf(mapping.entries.map { (a, b) -> a to b })
}


/**
 * Converts a read-only or mutable substitution to an immutable one.
 * If the receiver is already immutable, it is returned as-is.
 *
 * @receiver The substitution for which to return an immutable version.
 * @return The immutable substitution.
 */
fun Substitution.toImmutableSubstitution(): ImmutableSubstitution {
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
    val newSubstitution = MutableUnionFindSubstitution(this.termBuilder)
    this.variables.forEach { variable ->
        newSubstitution[variable] = this[variable]
    }
    return newSubstitution
}

/**
 * An empty substitution.
 *
 * @property termBuilder The term builder to use for creating/instantiating terms.
 */
internal class EmptySubstitution(
    override val termBuilder: TermBuilder,
) : SubstitutionBase(), ImmutableSubstitution {

    override fun isEmpty(): Boolean =
        true

    override val variables: Set<TermVar> get() =
        emptySet()

    override fun get(variable: TermVar, instantiate: Boolean): Term = variable

    override fun find(variable: TermVar): TermVar? = null

    override fun toMap(instantiate: Boolean): Map<Set<TermVar>, Term> =
        emptyMap()

    override fun toString(): String = "∅"

}

/**
 * A substitution that maps a single variable to a term.
 *
 * @property termBuilder The term builder to use for creating/instantiating terms.
 * @property from The variable to substitute.
 * @property to The term to substitute with.
 */
internal class SingletonSubstitution(
    override val termBuilder: TermBuilder,
    private val from: TermVar,
    private val to: Term,
) : SubstitutionBase(), ImmutableSubstitution {

    override fun isEmpty(): Boolean =
        false

    override val variables: Set<TermVar> get() =
        setOf(from)

    override fun get(variable: TermVar, instantiate: Boolean): Term =
        if (variable == from) to else variable

    override fun find(variable: TermVar): TermVar? =
        if (variable == from) from else null

    override fun toMap(instantiate: Boolean): Map<Set<TermVar>, Term> =
        mapOf(setOf(from) to to)

    override fun toString(): String =
        "{$from |-> $to}"

}