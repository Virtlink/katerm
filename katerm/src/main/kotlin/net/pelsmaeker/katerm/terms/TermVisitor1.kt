package net.pelsmaeker.katerm.terms

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

    fun visitConsList(term: ConsListTerm<Term>, arg: A): R

    fun visitNilList(term: NilListTerm, arg: A): R

    fun visitConcatList(term: ConcatListTerm<Term>, arg: A): R

    fun visitSomeOption(term: SomeOptionTerm<Term>, arg: A): R

    fun visitNoneOption(term: NoneOptionTerm, arg: A): R

    fun visitVar(term: TermVar, arg: A): R

}