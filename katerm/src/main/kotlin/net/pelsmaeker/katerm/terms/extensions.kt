package net.pelsmaeker.katerm.terms


/**
 * Determines whether this nullable term and optionally its subterms represent the same value
 * as the given term and it subterms, regardless of the actual implementations
 * of the terms and its subterms.
 *
 * Implementations should compare equal to other implementations of the same term type,
 * but can take shortcuts when comparing to the same implementation of the term type.
 *
 * @receiver The term to compare, which may be `null`.
 * @param that The other term to compare to, which may be `null`.
 * @param compareSubterms Whether to compare subterms.
 * @param compareAttachments Whether to compare the attachments.
 * @return `true` if the terms are equal (optionally modulo subterms/attachments); otherwise, `false`.
 */
fun Term?.equals(that: Term?, compareSubterms: Boolean, compareAttachments: Boolean): Boolean {
    if (this == null && that == null) return true
    if (this == null || that == null) return false
    return this.equals(that, compareSubterms = compareSubterms, compareAttachments = compareAttachments)
}



/**
 * Helper function to build a term using a [TermBuilderHelper].
 *
 * @param termBuilder The [TermBuilder] to use for building the term.
 * @param body The lambda that defines the term structure.
 * @return The return value.
 */
inline fun <R> withTermBuilder(
    termBuilder: TermBuilder = SimpleTermBuilder(),
    body: TermBuilderHelper.() -> R,
): R = with(TermBuilderHelper(termBuilder), body)


/**
 * Helper function to build a term using a [TermBuilderHelper].
 *
 * @receiver The [TermBuilder] to use for building the term.
 * @param body The lambda that defines the term structure.
 * @return The built term.
 */
inline fun <T: Term> TermBuilder.buildTerm(
    body: TermBuilderHelper.() -> T,
): T = with(TermBuilderHelper(this), body)


/**
 * Creates a term variable that is fresh within the given context.
 *
 * @param context The context in which the term variable is created.
 * @param name An optional base name for the term variable; if not provided, a default name will be used.
 * @return A new [TermVar] instance that is fresh in the given context.
 */
fun TermBuilder.freshVar(context: TermContext, name: String = "x"): TermVar {
    var nextVarId = 0
    val termVar = newVar(name)
    if (termVar !in context.termVars) return termVar
    nextVarId += 1

    while (true) {
        val termVar = newVar("$name$nextVarId")
        if (termVar !in context.termVars) return termVar
        nextVarId += 2
    }
}