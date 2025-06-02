package net.pelsmaeker.katerm

import net.pelsmaeker.katerm.terms.ApplTerm
import net.pelsmaeker.katerm.terms.ConcatListTerm
import net.pelsmaeker.katerm.terms.ConsListTerm
import net.pelsmaeker.katerm.terms.IntTerm
import net.pelsmaeker.katerm.terms.NilListTerm
import net.pelsmaeker.katerm.terms.NoneOptionTerm
import net.pelsmaeker.katerm.terms.RealTerm
import net.pelsmaeker.katerm.terms.SomeOptionTerm
import net.pelsmaeker.katerm.terms.StringTerm
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar

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
