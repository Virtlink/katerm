package net.pelsmaeker.katerm.generator.ast

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

data class IRFileUnit(
    val packageName: String,
    val decls: List<IRDecl>,
) {
    val sorts: List<SortInterface> get() = decls.filterIsInstance<SortInterface>()
    val constructors: List<ConstructorClass> get() = decls.filterIsInstance<ConstructorClass>()
}

sealed class IRDecl {
    abstract val type: TypeName
    abstract val superSort: SortInterface?
    abstract val decl: Decl
}

data class SortInterface(
    override val type: ClassName,
    override val superSort: SortInterface?,
    override val decl: SortDecl,
) : IRDecl()

data class ConstructorClass(
    override val type: ClassName,
    override val superSort: SortInterface?,
    val parameters: List<ParameterDef>,
    override val decl: RuleDecl,
) : IRDecl()

data class ParameterDef(
    val name: String,
    val type: TypeName,
)

//sealed class TypeSpec {
//    class TypeName(
//        val typeName: com.squareup.kotlinpoet.TypeName,
//    ) : TypeSpec()
//
//    class Type(
//        val type: net.pelsmaeker.katerm.generator.ast.Type,
//    ) : TypeSpec()
//
//    class KClass(
//        val kClass: kotlin.reflect.KClass<*>,
//    ) : TypeSpec()
//}
