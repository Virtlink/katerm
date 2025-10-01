package net.pelsmaeker.katerm.regex

import net.pelsmaeker.katerm.regex.RegexNfa.State
import kotlin.collections.plus

class RegexNfaBuilder<T, M> : RegexBuilder<RegexNfa<T, M>, T, M> {

    override fun epsilon(): RegexNfa<T, M> =
        Epsilon()

    private class Epsilon<T, M>(): RegexNfaImpl<T, M>() {
        override val initialState: State = State("ε_0")
        override val acceptingState: State = State("ε_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Transition to the accepting state without matching anything
                RegexNfa.Transition<T, M>(acceptingState, null),
            ),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "ε"
    }

    override fun atom(matcher: Matcher<T, M>): RegexNfa<T, M> =
        Atom(matcher)

    private class Atom<T, M>(val matcher: Matcher<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${matcher}_0")
        override val acceptingState: State = State("${matcher}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = mapOf(
            initialState to listOf(
                // Transition to the accepting state on the given matcher
                RegexNfa.Transition<T, M>(acceptingState, matcher),
            ),
            acceptingState to emptyList(),
        )

        override fun toString(): String = matcher.toString()
    }

    override fun concat(first: RegexNfa<T, M>, second: RegexNfa<T, M>): RegexNfa<T, M> =
        Concat(first, second)

    private class Concat<T, M>(val first: RegexNfa<T, M>, val second: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = first.transitions + second.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the initial state of the first NFA
                RegexNfa.Transition(first.initialState, null),
            ),
            first.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition from the accepting state of the first NFA
                // to the initial state of the second NFA
                RegexNfa.Transition(second.initialState, null),
            ) + first.getTransitions(first.acceptingState),
            second.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ) + second.getTransitions(second.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "$first · $second"
    }

    override fun union(first: RegexNfa<T, M>, second: RegexNfa<T, M>): RegexNfa<T, M> =
        Union(first, second)

    private class Union<T, M>(val first: RegexNfa<T, M>, val second: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = first.transitions + second.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the initial state of the first NFA
                RegexNfa.Transition(first.initialState, null),
                // Epsilon transition to the initial state of the second NFA
                RegexNfa.Transition(second.initialState, null),
            ),
            first.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ) + first.getTransitions(first.acceptingState),
            second.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state
                RegexNfa.Transition(acceptingState, null),
            ) + second.getTransitions(second.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($first | $second)"
    }

    override fun star(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        Star(pattern)

    private class Star<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
                // Epsilon transition to the accepting state (for zero repetitions)
                RegexNfa.Transition(acceptingState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)*"
    }

    override fun starLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        StarLazy(pattern)

    private class StarLazy<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the accepting state (for zero repetitions)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)*?"
    }

    override fun plus(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        Plus(pattern)

    private class Plus<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)+"
    }

    override fun plusLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        PlusLazy(pattern)

    private class PlusLazy<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one or more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state (to stop repeating)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition back to the NFA's initial state (for more repetitions)
                RegexNfa.Transition(pattern.initialState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)+?"
    }

    override fun question(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        Question(pattern)

    private class Question<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the NFA's initial state (for one occurrence)
                RegexNfa.Transition(pattern.initialState, null),
                // Epsilon transition to the accepting state (for zero occurrences)
                RegexNfa.Transition(acceptingState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state (to stop)
                RegexNfa.Transition(acceptingState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)?"
    }

    override fun questionLazy(pattern: RegexNfa<T, M>): RegexNfa<T, M> =
        QuestionLazy(pattern)

    private class QuestionLazy<T, M>(val pattern: RegexNfa<T, M>): RegexNfaImpl<T, M>() {
        override val initialState: State = State("${this}_0")
        override val acceptingState: State = State("${this}_1")
        override val transitions: Map<State, List<RegexNfa.Transition<T, M>>> = pattern.transitions + mapOf(
            initialState to listOf(
                // Epsilon transition to the accepting state (for zero occurrences)
                RegexNfa.Transition(acceptingState, null),
                // Epsilon transition to the NFA's initial state (for one occurrence)
                RegexNfa.Transition(pattern.initialState, null),
            ),
            pattern.acceptingState to listOf<RegexNfa.Transition<T, M>>(
                // Epsilon transition to the accepting state (to stop)
                RegexNfa.Transition(acceptingState, null),
            ) + pattern.getTransitions(pattern.acceptingState),
            acceptingState to emptyList(),
        )

        override fun toString(): String = "($pattern)??"
    }

    private abstract class RegexNfaImpl<T, M> : RegexNfa<T, M> {

        abstract override val initialState: State
        abstract override val acceptingState: State
        abstract override val transitions: Map<State, List<RegexNfa.Transition<T, M>>>

        final override fun getTransitions(state: State): List<RegexNfa.Transition<T, M>> {
            return transitions[state] ?: throw IllegalArgumentException("State '$state' does not exist in this NFA.")
        }
    }
}