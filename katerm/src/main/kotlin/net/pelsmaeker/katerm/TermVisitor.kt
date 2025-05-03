package net.pelsmaeker.katerm

/**
 * Visits terms.
 */
interface TermVisitor<R> {

    fun visitTerm(term: Term): R

    fun visitValue(term: ValueTerm): R

    fun visitInt(term: IntTerm): R

    fun visitReal(term: RealTerm): R

    fun visitString(term: StringTerm): R

    fun visitAppl(term: ApplTerm): R

    fun visitList(term: ListTerm<Term>): R

    fun visitOption(term: OptionTerm<Term>): R

    fun visitVar(term: TermVar): R

}

/**
 * Visits terms. Supports one argument.
 */
interface TermVisitor1<A, R> {

    fun visitTerm(term: Term, arg: A): R

    fun visitValue(term: ValueTerm, arg: A): R

    fun visitInt(term: IntTerm, arg: A): R

    fun visitReal(term: RealTerm, arg: A): R

    fun visitString(term: StringTerm, arg: A): R

    fun visitAppl(term: ApplTerm, arg: A): R

    fun visitList(term: ListTerm<Term>, arg: A): R

    fun visitOption(term: OptionTerm<Term>, arg: A): R

    fun visitVar(term: TermVar, arg: A): R

}
