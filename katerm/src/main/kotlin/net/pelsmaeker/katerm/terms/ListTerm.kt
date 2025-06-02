package net.pelsmaeker.katerm.terms

/**
 * A list term.
 *
 * Note: a [TermVar] is also a [ListTerm], so take care when matching against this interface type.
 *
 * @param E The type of the elements in the list.
 */
sealed interface ListTerm<out E: Term> : Term {

    /**
     * Whether the list is definitely empty (and contains no list variables).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isEmpty(): Boolean

    /**
     * Whether the list is definitely not empty (and not just a list variable).
     *
     * Note that [isNotEmpty] is not the inverse of [isEmpty]. It is possible for both to be false.
     */
    fun isNotEmpty(): Boolean

    /** The minimum number of elements in the list. This is the number of elements in [elements]. */
    val minSize: Int

    /** The number of elements in the list; or `null` if the list contains one or more term variables. */
    val size: Int?

    /** The elements in the list. If the list contains term variables, they are not included here. */
    val elements: List<E>

}