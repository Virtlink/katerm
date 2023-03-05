package net.pelsmaeker.katerm

/**
 * Visits terms.
 */
interface TermVisitor<R> {

    fun visitInt(term: IntTerm): R

    fun visitReal(term: RealTerm): R

    fun visitString(term: StringTerm): R

    fun visitAppl(term: ApplTerm): R

    fun visitList(term: ListTerm): R

    fun visitVar(term: TermVar): R

}

/**
 * Visits terms. Supports one argument.
 */
interface TermVisitor1<A, R> {

    fun visitInt(term: IntTerm, arg: A): R

    fun visitReal(term: RealTerm, arg: A): R

    fun visitString(term: StringTerm, arg: A): R

    fun visitAppl(term: ApplTerm, arg: A): R

    fun visitList(term: ListTerm, arg: A): R

    fun visitVar(term: TermVar, arg: A): R

}
