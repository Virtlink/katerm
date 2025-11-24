package net.pelsmaeker.katerm.generator.ast

data class FileUnit(
    val packageName: String,
    val declarations: List<Decl>,
)

sealed interface Decl {
    val name: String
}

data class SortDecl(
    override val name: String,
    val varSpecs: List<VarSpec>,
    var types: List<Type>,
) : Decl

data class ConsDecl(
    override val name: String,
    val varSpecs: List<VarSpec>,
    var types: List<Type>,
) : Decl

data class TemplateDecl(
    override val name: String,
    val templateText: String,
) : Decl

data class VarSpec(
    val name: String,
    val typeSpec: TypeSpec,
)

sealed interface TypeSpec

data class StarTypeSpec(
    val type: Type,
) : TypeSpec

data class SimpleTypeSpec(
    val type: Type,
) : TypeSpec

sealed interface Type

data class RefType(
    val name: String,
) : Type

data object IntType : Type

data object StringType : Type

//data class ResolvedRefType(
//    val declType: ClassName,
////    val decl: Decl,
//) : Type
