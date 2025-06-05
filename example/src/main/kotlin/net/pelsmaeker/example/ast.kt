package net.pelsmaeker.example

import net.pelsmaeker.katerm.attachments.TermAttachments
import net.pelsmaeker.katerm.ksp.GenerateApplTerm
import net.pelsmaeker.katerm.terms.ApplTerm
import net.pelsmaeker.katerm.terms.ApplTermBase
import net.pelsmaeker.katerm.terms.IntTerm
import net.pelsmaeker.katerm.terms.ListTerm
import net.pelsmaeker.katerm.terms.StringTerm
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVisitor
import net.pelsmaeker.katerm.terms.TermVisitor1
import java.util.Objects

interface JoeTerm : Term {

//    fun <R> accept(visitor: JoeTermVisitor<R>): R
//
//    fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R

}

//@GenerateApplTerm("Unit")
interface JoeUnit : JoeTerm, ApplTerm {

    val modules: List<JoeModule>

}

//@GenerateApplTerm("Module")
interface JoeModule : JoeTerm, ApplTerm {

    val name: String
}

//@GenerateApplTerm
//private abstract class JoeModulePrototype(
//    val name: String,
//) : JoeTerm, ApplTerm

//operator fun JoeUnit.component1(): List<JoeModule> = modules
//
//class JoeUnitImpl internal constructor(
//    private val modulesTerm: ListTerm<JoeModule>,
//    termAttachments: TermAttachments,
//) : JoeTerm, ApplTerm, ApplTermBase(termAttachments) {
//
//    companion object {
//        const val OP: String = "Unit"
//        const val ARITY: Int = 1
//    }
//
//    val modules: List<JoeModule> get() = modulesTerm.elements
//
//    operator fun component1(): List<JoeModule> = modules
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = listOf(modulesTerm)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
////    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitUnit(this)
////
////    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitUnit(this, arg)
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is JoeUnit) return false
//
//        return this.modulesTerm.equals(that.modules, compareAttachments = compareAttachments)
//    }
//
//    override val subtermsHashCode: Int = Objects.hash(
//        modulesTerm,
//    )
//}

//
//class JoeModule internal constructor(
//    private val nameTerm: StringTerm,
//    private val declarationsTerm: ListTerm<JoeDecl>,
//    termAttachments: TermAttachments
//) : JoeTerm, ApplTerm, ApplTermBase(termAttachments) {
//
//    companion object {
//        const val OP: String = "Module"
//        const val ARITY: Int = 2
//    }
//
//    val name: String get() = nameTerm.value
//    val declarations: List<JoeDecl> get() = declarationsTerm.elements
//
//    operator fun component1(): String = name
//    operator fun component2(): List<JoeDecl> = declarations
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = listOf(nameTerm, declarationsTerm)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
//    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitModule(this)
//
//    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitModule(this, arg)
//
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is JoeModule) return false
//
//        return this.nameTerm.equals(that.nameTerm, compareAttachments = compareAttachments)
//                && this.declarationsTerm.equals(that.declarationsTerm, compareAttachments = compareAttachments)
//    }
//
//    override val subtermsHashCode: Int = Objects.hash(
//        nameTerm,
//        declarationsTerm,
//    )
//}
//
//sealed interface JoeDecl : JoeTerm, Term
//
//
//class DefDecl internal constructor(
//    private val nameTerm: StringTerm,
//    private val typeTerm: JoeType,
//    private val bodyTerm: JoeExpr,
//    termAttachments: TermAttachments,
//) : JoeDecl, ApplTerm, ApplTermBase(termAttachments) {
//
//    companion object {
//        const val OP: String = "DefDecl"
//        const val ARITY: Int = 3
//    }
//
//    val name: String get() = nameTerm.value
//    val type: JoeType get() = typeTerm
//    val body: JoeExpr get() = bodyTerm
//
//    operator fun component1(): String = name
//    operator fun component2(): JoeType = type
//    operator fun component3(): JoeExpr = body
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = listOf(nameTerm, typeTerm, bodyTerm)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
//    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitDefDecl(this)
//
//    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitDefDecl(this, arg)
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is DefDecl) return false
//
//        return this.nameTerm.equals(that.nameTerm, compareAttachments = compareAttachments)
//                && this.typeTerm.equals(that.typeTerm, compareAttachments = compareAttachments)
//                && this.bodyTerm.equals(that.bodyTerm, compareAttachments = compareAttachments)
//    }
//
//    override val subtermsHashCode: Int = Objects.hash(
//        nameTerm,
//        typeTerm,
//        bodyTerm,
//    )
//}
//
//sealed interface JoeType : JoeTerm, Term
//
//
//class IntType internal constructor(
//    termAttachments: TermAttachments,
//) : JoeType, ApplTerm, ApplTermBase(termAttachments) {
//
//    companion object {
//        const val OP: String = "IntType"
//        const val ARITY: Int = 0
//    }
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = emptyList()
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
//    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitIntType(this)
//
//    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitIntType(this, arg)
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is IntType) return false
//
//        return true
//    }
//
//    override val subtermsHashCode: Int = 0
//
//}
//
//
//interface JoeExpr : JoeTerm, Term
//
//
//class IntLiteralExpr internal constructor(
//    private val valueTerm: IntTerm,
//    termAttachments: TermAttachments,
//) : JoeExpr, ApplTerm, ApplTermBase(termAttachments) {
//
//    companion object {
//        const val OP: String = "IntLiteralExpr"
//        const val ARITY: Int = 1
//    }
//
//    val value: Int get() = valueTerm.value
//
//    operator fun component1(): Int = value
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = listOf(valueTerm)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
//    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitIntLiteralExpr(this)
//
//    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitIntLiteralExpr(this, arg)
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is IntLiteralExpr) return false
//
//        return this.valueTerm.equals(that.valueTerm, compareAttachments = compareAttachments)
//    }
//
//    override val subtermsHashCode: Int = Objects.hash(
//        valueTerm,
//    )
//
//}
//
//class RefExpr internal constructor(
//    private val nameTerm: StringTerm,
//    termAttachments: TermAttachments,
//) : JoeExpr, ApplTerm, ApplTermBase(termAttachments) {
//    companion object {
//        const val OP: String = "RefExpr"
//        const val ARITY: Int = 1
//    }
//
//    val name: String get() = nameTerm.value
//
//    operator fun component1(): String = name
//
//    override val termOp: String get() = OP
//
//    override val termArity: Int get() = ARITY
//
//    override val termArgs: List<Term> get() = listOf(nameTerm)
//
//    override fun <R> accept(visitor: TermVisitor<R>): R = visitor.visitAppl(this)
//
//    override fun <A, R> accept(visitor: TermVisitor1<A, R>, arg: A): R = visitor.visitAppl(this, arg)
//
//    override fun <R> accept(visitor: JoeTermVisitor<R>): R = visitor.visitRefExpr(this)
//
//    override fun <A, R> accept(visitor: JoeTermVisitor1<A, R>, arg: A): R = visitor.visitRefExpr(this, arg)
//
//    override fun equalSubterms(that: ApplTerm, compareAttachments: Boolean): Boolean {
//        if (that !is RefExpr) return false
//
//        return this.nameTerm.equals(that.nameTerm, compareAttachments = compareAttachments)
//    }
//
//    override val subtermsHashCode: Int = Objects.hash(
//        nameTerm,
//    )
//
//}