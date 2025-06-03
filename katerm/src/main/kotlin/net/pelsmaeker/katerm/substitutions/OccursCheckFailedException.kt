package net.pelsmaeker.katerm.substitutions

import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar

/**
 * Exception thrown when the occurs check fails during unification.
 *
 * This exception indicates that a variable occurs within itself,
 *
 * @property variable The variable that was being unified.
 * @property occurrence The variable that was found to occur within [term].
 * @property representation The representative variable of both [variable] and [occurrence].
 * @property term The term in which the occurs check failed.
 * @param message Optional custom message for the exception; or `null` for a default message.
 * @param cause Optional cause of the exception; or `null` if none.
 */
class OccursCheckFailedException(
    val variable: TermVar,
    val occurrence: TermVar,
    val representation: TermVar,
    val term: Term,
    message: String? = null,
    cause: Throwable? = null
) : IllegalStateException(
    message ?: "Occurs check failed: $variable occurs as $occurrence in the term (both represented by $representation): $term",
    cause
)
