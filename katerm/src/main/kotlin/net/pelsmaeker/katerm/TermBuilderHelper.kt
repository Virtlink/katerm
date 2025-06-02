package net.pelsmaeker.katerm

/**
 * Helper function to build a term using a [TermBuilderHelper].
 *
 * @param termBuilder The [TermBuilder] to use for building the term.
 * @param body The lambda that defines the term structure.
 * @return The built term.
 */
inline fun <R> withTermBuilder(
    termBuilder: TermBuilder = SimpleTermBuilder(),
    body: TermBuilderHelper.() -> R,
): R = with(TermBuilderHelper(termBuilder), body)

/**
 * Helper function to build a term using a [TermBuilderHelper].
 *
 * @param termBuilder The [TermBuilder] to use for building the term.
 * @param body The lambda that defines the term structure.
 * @return The built term.
 */
@Suppress("FunctionName")
inline fun <R : Term> T(
    termBuilder: TermBuilder = SimpleTermBuilder(),
    body: TermBuilderHelper.() -> R,
): R = withTermBuilder(termBuilder, body)

/**
 * Helper class to build terms using a [TermBuilder].
 *
 * @property termBuilder The [TermBuilder] used to create terms.
 */
class TermBuilderHelper(
    private val termBuilder : TermBuilder,
): TermBuilder by termBuilder {

    /**
     * Creates a new term variable with the given name.
     *
     * @receiver The name of the term variable.
     * @return A new [TermVar] instance with the specified name.
     */
    operator fun String.not(): TermVar = termBuilder.newVar(this)

    /**
     * Creates a new string term.
     *
     * @param value The value of the string term.
     * @return A new [StringTerm] instance with the specified value.
     */
    fun string(value: String): StringTerm = termBuilder.newString(value)

    /**
     * Creates a new integer term.
     *
     * @param value The value of the integer term.
     * @return A new [IntTerm] instance with the specified value.
     */
    fun int(value: Int): IntTerm = termBuilder.newInt(value)

    /**
     * Creates a new real term.
     *
     * @param value The value of the real term.
     * @return A new [RealTerm] instance with the specified value.
     */
    fun real(value: Double): RealTerm = termBuilder.newReal(value)

    /**
     * Creates a new real term from a float value.
     *
     * @param value The value of the real term.
     * @return A new [RealTerm] instance with the specified value.
     */
    fun real(value: Float): RealTerm = termBuilder.newReal(value.toDouble())

    /**
     * Creates an empty list term.
     *
     * @return A new [NilListTerm] instance representing an empty list.
     */
    fun list(): NilListTerm = termBuilder.newEmptyList()

    /**
     * Creates a list term containing the specified terms.
     *
     * @param terms The terms to include in the list.
     * @return A new [ListTerm] instance containing the specified terms.
     */
    fun <E: Term> list(vararg terms: E): ListTerm<E> = termBuilder.newListOf(*terms)

    /**
     * Creates a new option term with the specified term.
     *
     * @param term The term to include in the option.
     * @return A new [OptionTerm] instance containing the specified term.
     */
    fun <E: Term> some(term: E): OptionTerm<E> = termBuilder.newOption(term)

    /**
     * Creates a new empty option term.
     *
     * @return A new [NoneOptionTerm] instance representing an empty option.
     */
    fun none(): NoneOptionTerm = termBuilder.newEmptyOption()

    /**
     * Creates a new list from a head term and a tail list term.
     *
     * @receiver The head term to include in the list.
     * @param list The tail list term to include in the list.
     * @return A new [ListTerm] instance containing the head term and the tail list.
     */
    operator fun <E: Term> E.rangeTo(list: ListTerm<E>): ListTerm<E> = termBuilder.newList(this, list)

    /**
     * Creates a new application term with the specified operator and arguments.
     *
     * @receiver The operator of the application term.
     * @param args The arguments to include in the application term.
     * @return A new [ApplTerm] instance with the specified operator and arguments.
     */
    operator fun String.invoke(vararg args: Term): ApplTerm = termBuilder.newAppl(this, *args)

}
