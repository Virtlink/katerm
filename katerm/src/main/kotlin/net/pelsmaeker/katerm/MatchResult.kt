package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar

/**
 * Describes the result of a successful match of a term to a pattern,
 * where the pattern may contain term variables.
 */
data class MatchResult(
    /** The terms associated to the term variables in the pattern. */
    val associations: Map<TermVar, Term>
)