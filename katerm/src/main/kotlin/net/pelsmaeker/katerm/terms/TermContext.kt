package net.pelsmaeker.katerm.terms

/**
 * A context in which terms are used.
 */
interface TermContext {

    /** The set of term variables that occur in this context. */
    val termVars: Set<TermVar>

}


