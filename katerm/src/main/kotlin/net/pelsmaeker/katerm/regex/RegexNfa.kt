package net.pelsmaeker.katerm.regex

interface RegexNfa {
    val startState: State

    interface State {
        val isAccepting: Boolean
        val isFinal: Boolean
        val transitions: List<Transition>
    }

    interface Transition {
        val toState: State
        val matcher: Matcher?
    }
}

