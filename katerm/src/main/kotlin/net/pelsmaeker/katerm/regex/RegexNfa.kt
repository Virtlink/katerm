package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution

/**
 * A non-deterministic finite automaton (NFA) for regular expressions.
 *
 * @param T The type of input tokens to match against.
 * @param M The type of metadata associated with each state.
 */
interface RegexNfa<T, M> : Regex<T, M> {

    /** The initial state of the NFA. */
    val initialState: State

    /** The accepting state of the NFA. */
    val acceptingState: State

    /**
     * Gets the transitions from the given state.
     *
     * @param state The state to get the transitions from.
     * @return The transitions from the given state, in order of priority (highest priority first).
     */
    fun getTransitions(state: State): List<Transition<T, M>>

    /**
     * A state in the NFA.
     */
    class State {
        override fun equals(other: Any?): Boolean {
            return this === other
        }

        override fun hashCode(): Int {
            return System.identityHashCode(this)
        }

        override fun toString(): String {
            return "State@${Integer.toHexString(hashCode())}"
        }
    }

    /**
     * A transition from one state to another.
     *
     * @property toState The state to transition to.
     * @property matcher The matcher that must match for the transition to be taken;
     * or `null` for an epsilon transition.
     */
    data class Transition<T, M>(
        val toState: State,
        val matcher: Matcher<T, M>?,
    )

    override fun buildMatcher(initialMetadata: M): RegexMatcher<T, M> {
        return RegexMatcherImpl<T, M>(this, initialMetadata)
    }

    /**
     * Implementation of [RegexMatcher].
     *
     * @property states An ordered map from states to their corresponding substitutions,
     * ordered from highest to lowest priority.
     */
    private class RegexMatcherImpl<T, M> private constructor(
        private val nfa: RegexNfa<T, M>,
        private val states: LinkedHashMap<State, M>,
    ) : RegexMatcher<T, M> {

        /**
         * Creates a new regex matcher starting in the given initial state with the given initial substitution.
         *
         * @param nfa The NFA.
         * @param initialMetadata The initial metadata.
         */
        constructor(nfa: RegexNfa<T, M>, initialMetadata: M) : this(
            nfa,
            // We close over the epsilon transitions from the initial state,
            // such that we start in all reachable states.
            closeOverEpsilonTransitions(nfa, linkedMapOf(nfa.initialState to initialMetadata)),
        )

        override fun getAcceptingMetadata(): M? {
            return states.firstNotNullOfOrNull { (state, metadata) ->
                if (nfa.acceptingState == state) metadata else null
            }
        }

        override fun match(input: T): RegexMatcher<T, M> {
            val newStates = LinkedHashMap<State, M>()
            for ((state, metadata) in states) {
                for (transition in nfa.getTransitions(state)) {
                    val matcher = transition.matcher
                    val newMetadata = if (matcher != null) matcher.matches(input, metadata) else metadata
                    if (newMetadata != null) {
                        newStates[transition.toState] = newMetadata
                    }
                }
            }
            // Close over epsilon transitions again.
            closeOverEpsilonTransitions(nfa, newStates)
            return RegexMatcherImpl(nfa, newStates)
        }

        override fun isAccepting(): Boolean {
            return states.keys.any { nfa.acceptingState == it }
        }

        override fun isEmpty(): Boolean {
            return states.isEmpty()
        }

        companion object {
            /**
             * Closes over epsilon transitions in the given set of states.
             *
             * This mutates the map in-place.
             *
             * @param nfa The NFA.
             * @param states The set of states to close over epsilon transitions.
             * @return The set of states, mapped to substitutions.
             */
            private fun <T, M> closeOverEpsilonTransitions(
                nfa: RegexNfa<T, M>,
                states: LinkedHashMap<State, M>
            ): LinkedHashMap<State, M> {
                val stack = states.keys.toMutableList()
                while (stack.isNotEmpty()) {
                    val state = stack.removeAt(stack.size - 1)
                    val substitution = states[state]!!
                    for (transition in nfa.getTransitions(state)) {
                        if (transition.matcher == null) {
                            val toState = transition.toState
                            if (toState !in states) {
                                states[toState] = substitution
                                stack.add(toState)
                            }
                        }
                    }
                }
                return states
            }
        }

    }
}
