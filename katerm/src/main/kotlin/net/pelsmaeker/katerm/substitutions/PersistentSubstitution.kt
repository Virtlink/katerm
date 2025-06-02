package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar

/**
 * A persistent substitution is an immutable mapping of variables to terms
 * where modifications return a new substitution instead of modifying the original.
 */
interface PersistentSubstitution : ImmutableSubstitution {

    /**
     * Returns a new substitution with the given variable mapped to the specified term.
     *
     * @param variable The variable to map.
     * @param term The term to map the variable to.
     * @return A new substitution with the updated mapping; or `this` if the mapping is unchanged.
     */
    fun set(variable: TermVar, term: Term): PersistentSubstitution

    /**
     * Returns a new substitution without the mapping for the given variable.
     *
     * @param variable The variable to remove.
     * @return A new substitution without the specified mapping; or `this` if the mapping is unchanged.
     */
    fun remove(variable: TermVar): PersistentSubstitution

    /**
     * Returns a new substitution with all mappings cleared.
     *
     * @return A new empty substitution.
     */
    fun clear(): PersistentSubstitution

    /**
     * Returns a new builder with the same contents as this substitution.
     *
     * The builder can be used to efficiently perform multiple modification operations.
     */
    fun builder(): Builder

    /**
     * A builder of the persistent substitution. The builder exposes its modification operations through the [MutableSubstitution] interface.
     *
     * Builders are reusable, that is [build] method can be called multiple times with modifications between these calls.
     * However, applied modifications do not affect previously built persistent collection instances.
     */
    interface Builder: MutableSubstitution {

        /**
         * Builds the persistent substitution with the same contents as this builder.
         *
         * This method can be applied multiple times.
         *
         * @return The built persistent substitution;
         * or the same instance as returned previously if no modifications were made;
         * or the same instance as this builder was based on if no modifications were made and [build] was not previously called.
         */
        fun build(): PersistentSubstitution
    }

}