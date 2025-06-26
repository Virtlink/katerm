package net.pelsmaeker.katerm.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

@Suppress("FunctionName")
object Katerm {

    val packageName = "net.pelsmaeker.katerm.terms"

    val Term = ClassName(packageName, "Term")
    val ApplTerm = ClassName(packageName, "ApplTerm")
    val IntTerm = ClassName(packageName, "IntTerm")
    val StringTerm = ClassName(packageName, "StringTerm")
    val ApplTermBase = ClassName(packageName, "ApplTermBase")
    val TermAttachments = ClassName(packageName, "TermAttachments")
    private val ListTerm = ClassName(packageName, "ListTerm")
    private val OptionTerm = ClassName(packageName, "OptionTerm")
    private val TermVisitor = ClassName(packageName, "TermVisitor")
    private val TermVisitor1 = ClassName(packageName, "TermVisitor1")

    fun ListTerm(arg1: TypeName) = ListTerm.parameterizedBy(arg1)
    fun OptionTerm(arg1: TypeName) = OptionTerm.parameterizedBy(arg1)
    fun TermVisitor(arg1: TypeName) = TermVisitor.parameterizedBy(arg1)
    fun TermVisitor1(arg1: TypeName, arg2: TypeName) = TermVisitor1.parameterizedBy(arg1, arg2)

}