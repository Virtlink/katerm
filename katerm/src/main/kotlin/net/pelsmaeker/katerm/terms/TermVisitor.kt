package net.pelsmaeker.katerm.terms

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

    fun visitConsList(term: ConsListTerm<Term>): R

    fun visitNilList(term: NilListTerm): R

    fun visitConcatList(term: ConcatListTerm<Term>): R

    fun visitSomeOption(term: SomeOptionTerm<Term>): R

    fun visitNoneOption(term: NoneOptionTerm): R

    fun visitVar(term: TermVar): R

}

