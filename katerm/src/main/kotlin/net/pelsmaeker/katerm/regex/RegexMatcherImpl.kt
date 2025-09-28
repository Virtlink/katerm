package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.terms.Term

class RegexMatcherImpl private constructor(
    override val substitution: Substitution,
    private val nfa: RegexNfa,
    // The list is ordered from highest to lowest priority
    private val states: List<State>,
) : RegexMatcher {

    override fun match(input: Term): RegexMatcher? {
        val newStates = mutableListOf<State>()
        for (state in states) {
            for (transition in state.transitions) {
                val matcher = transition.matcher
                val newSubstitution = if (matcher != null) matcher.matches(input, substitution) else substitution
                if (newSubstitution != null) {
                    return RegexMatcherImpl(newSubstitution, nfa, transition.toState)
                }
            }
        }
//        val stateClosure = epsilonClosure(states)
//        val newStates = mutableSetOf<State>()
//        for (state in stateClosure) {
//            // Follow non-epsilon transitions
//            for (transition in state.transitions) {
//                // FIXME: This is not correct given backtracking
//                val newSubstitution = transition.matcher.matches(input, substitution)
//                if (newSubstitution != null) {
//                    return RegexMatcherImpl(newSubstitution, nfa, transition.toState)
//                }
//            }
//        }
//        if (newStates.isEmpty()) return null
//        return RegexMatcherImpl(substitution, nfa, newStates)
    }

    /**
     * Follows all epsilon transitions from the given states,
     * adding all reachable states to the given set.
     *
     * @param states The set of states to compute the closure for.
     * @return The epsilon closure of the given states.
     */
    private fun epsilonClosure(states: List<State>): List<State> {
        val closure = states.toMutableList()
        val stack = ArrayDeque<State>()
        for (state in states) {
            stack.add(state)
        }
        while (stack.isNotEmpty()) {
            val state = stack.removeLast()
            val added = closure.add(state)
            if (added) {
                for (epsilonTransition in state.epsilonTransitions) {
                    stack.add(epsilonTransition)
                }
            }
        }
        return closure
    }

    override fun isAccepting(): Boolean {
        return state.isAccepting
    }

    override fun isFinal(): Boolean {
        return state.isFinal
    }

    /**
     * A state definition.
     *
     * @property isAccepting Whether this state is an accepting state.
     * @property isFinal Whether this state is a final state.
     */
    private class State(
        val isAccepting: Boolean,
        val isFinal: Boolean,
    ) {
        /**
         * The non-epsilon transitions from this state.
         * The highest-priority transition is at the start of the list.
         */
        val transitions: MutableList<Transition> = mutableListOf()
    }

    /**
     * A transition from one state to another.
     *
     * @property toState The state to transition to.
     * @property matcher The matcher that must match for the transition to be taken;
     * or `null` for an epsilon transition.
     */
    private data class Transition(
        val toState: State,
        val matcher: Matcher?,
    )

}