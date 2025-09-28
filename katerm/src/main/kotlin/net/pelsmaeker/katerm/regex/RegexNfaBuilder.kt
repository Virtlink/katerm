package net.pelsmaeker.katerm.regex

/**
 * Build an NFS for a regex matcher.
 */
class RegexNfaBuilder : RegexNfa {

    /**
     * The states in the regex matcher. The initial state is at index 0.
     */
    private val states: MutableList<StateImpl> = mutableListOf()

    override val startState: RegexNfa.State get() = states.first()

    /**
     * Adds a new state to the regex matcher.
     *
     * @param isAccepting Whether this state is an accepting state.
     * @param isFinal Whether this state is a final state.
     * @return The newly added state.
     */
    fun addState(isAccepting: Boolean, isFinal: Boolean): RegexNfa.State {
        val stateImpl = StateImpl(isAccepting, isFinal)
        states.add(stateImpl)
        return stateImpl
    }

    /**
     * Adds a transition from one state to another.
     *
     * @param fromState The state to transition from.
     * @param toState The state to transition to.
     * @param matcher The matcher that must match for the transition to be taken;
     * or `null` for an epsilon transition.
     */
    fun addTransition(fromState: RegexNfa.State, toState: RegexNfa.State, matcher: Matcher?) {
        require(fromState in states) { "fromState must be added first." }
        require(toState in states) { "toState must be added first." }
        fromState as StateImpl
        toState as StateImpl
        fromState.transitions.add(TransitionImpl(toState, matcher))
    }

    /**
     * Adds an epsilon transition from one state to another.
     *
     * @param fromState The state to transition from.
     * @param toState The state to transition to.
     */
    fun addEpsilonTransition(fromState: RegexNfa.State, toState: RegexNfa.State) {
        addTransition(fromState, toState, null)
    }

    fun build(): RegexNfa = this

    /**
     * A state definition.
     *
     * @property isAccepting Whether this state is an accepting state.
     * @property isFinal Whether this state is a final state.
     */
    private class StateImpl(
        override val isAccepting: Boolean,
        override val isFinal: Boolean,
    ) : RegexNfa.State {
        /**
         * The transitions from this state.
         * The highest-priority transition is at the start of the list.
         */
        override val transitions: MutableList<TransitionImpl> = mutableListOf()
    }

    /**
     * A transition from one state to another.
     *
     * @property toState The state to transition to.
     * @property matcher The matcher that must match for the transition to be taken;
     * or `null` for an epsilon transition.
     */
    private data class TransitionImpl(
        override val toState: StateImpl,
        override val matcher: Matcher?,
    ) : RegexNfa.Transition
}