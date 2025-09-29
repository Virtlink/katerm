package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.regex.RegexNfa.State

class RegexNfaBuilder<T, M> : RegexBuilder<RegexNfa<T, M>, T, M> {

    override fun atom(matcher: Matcher<T, M>): RegexNfa<T, M> = Atom(matcher)

    private class Atom<T, M>(val matcher: Matcher<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Transition to the accepting state on the given matcher
                RegexNfa.Transition<T, M>(acceptingState, matcher),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = matcher.toString()
    }

    override fun concat(first: RegexNfa<T, M>, second: RegexNfa<T, M>): RegexNfa<T, M> = Concat(first, second)

    private class Concat<T, M>(val first: RegexNfa<T, M>, val second: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the initial state of the first NFA
                RegexNfa.Transition(first.initialState, null),
            ),
            first.acceptingState to listOf(
                // Epsilon transition from the accepting state of the first NFA
                // to the initial state of the second NFA
                RegexNfa.Transition(second.initialState, null),
            ),
            second.acceptingState to listOf(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "$first · $second"
    }

    override fun union(first: RegexNfa<T, M>, second: RegexNfa<T, M>): RegexNfa<T, M> = Union(first, second)

    private class Union<T, M>(val first: RegexNfa<T, M>, val second: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the initial state of the first NFA
                RegexNfa.Transition(first.initialState, null),
                // Epsilon transition to the initial state of the second NFA
                RegexNfa.Transition(second.initialState, null),
            ),
            first.acceptingState to listOf(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ),
            second.acceptingState to listOf(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($first | $second)"
    }

    override fun star(pattern: RegexNfa<T, M>): RegexNfa<T, M> = Star(pattern)

    private class Star<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
                // Epsilon transition to the accepting state (for zero repetitions)
                RegexNfa.Transition(acceptingState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($nfa)*"
    }

    override fun starLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> = StarLazy(pattern)

    private class StarLazy<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the accepting state (for zero repetitions)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($nfa)*?"
    }

    override fun plus(pattern: RegexNfa<T, M>): RegexNfa<T, M> = Plus(pattern)

    private class Plus<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($nfa)+"
    }

    override fun plusLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> = PlusLazy(pattern)

    private class PlusLazy<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(nfa.initialState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($nfa)+?"
    }

    override fun question(pattern: RegexNfa<T, M>): RegexNfa<T, M> = Question(pattern)

    private class Question<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one occurrence)
                RegexNfa.Transition(nfa.initialState, null),
                // Epsilon transition to the accepting state (for zero occurrences)
                RegexNfa.Transition(acceptingState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition to the accepting state (to stop)
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }

        override fun toString(): String = "($nfa)?"
    }

    override fun questionLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> = QuestionLazy(pattern)

    private class QuestionLazy<T, M>(val nfa: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State()
        override val acceptingState: State = State()
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Epsilon transition to the accepting state (for zero occurrences)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition to the NFA's initial state (for one occurrence)
                RegexNfa.Transition(nfa.initialState, null),
            ),
            nfa.acceptingState to listOf(
                // Epsilon transition to the accepting state (to stop)
                RegexNfa.Transition(acceptingState, null),
            ),
        )

        override fun toString(): String = "($nfa)??"
    }

    private abstract class RegexNfaImpl<T, M> : RegexNfa<T, M> {

        abstract override val initialState: State
        abstract override val acceptingState: State
        protected abstract val transitions: Map<State, List<RegexNfa.Transition<T, M>>>

        override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            val transitions = transitions[state] ?: throw IllegalArgumentException("State $state not found.")
            return transitions
        }
    }
}