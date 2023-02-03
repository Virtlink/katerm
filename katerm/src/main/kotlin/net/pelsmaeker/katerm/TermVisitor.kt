package net.pelsmaeker.katerm

/**
 * Visits terms.
 */
interface TermVisitor<R> {

    fun visitInt(term: IntTerm): R

    fun visitString(term: StringTerm): R

    fun visitBlob(term: BlobTerm): R

    fun visitAppl(term: ApplTerm): R

    fun visitList(term: ListTerm): R

    fun visitCons(term: ConsTerm): R

    fun visitNil(term: NilTerm): R

    fun visitVar(term: TermVar): R

    fun visitListVar(term: ListTermVar): R

}