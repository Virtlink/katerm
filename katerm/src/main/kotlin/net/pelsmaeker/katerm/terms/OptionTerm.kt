package net.pelsmaeker.katerm.terms

/**
 * An option term.
 *
 * Note: a [TermVar] is also an [OptionTerm], so take care when matching against this interface type.
 *
 * @param E The type of the element in the option.
 */
sealed interface OptionTerm<out E : Term> : Term {

    /**
     * Whether the option is definitely empty (and not just an option variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isEmpty(): Boolean

    /**
     * Whether the option is definitely not empty (and not just an option variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isNotEmpty(): Boolean

}