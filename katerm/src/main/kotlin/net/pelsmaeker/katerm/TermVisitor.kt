package net.pelsmaeker.katerm

/**
 * Visits terms.
 *
 * @param R The return type of the visitor methods.
 */
interface TermVisitor<out R> {

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
 *
 * @param A The type of the argument passed to the visitor methods.
 * @param R The return type of the visitor methods.
 */
interface TermVisitor1<in A, out R> {

    fun visitInt(term: IntTerm, arg: A): R

    fun visitReal(term: RealTerm, arg: A): R

    fun visitString(term: StringTerm, arg: A): R

    fun visitAppl(term: ApplTerm, arg: A): R

    fun visitList(term: ListTerm<Term>, arg: A): R

    fun visitOption(term: OptionTerm<Term>, arg: A): R

    fun visitVar(term: TermVar, arg: A): R

}
