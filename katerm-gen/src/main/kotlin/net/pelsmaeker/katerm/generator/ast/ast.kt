package net.pelsmaeker.katerm.generator.ast

import com.squareup.kotlinpoet.ClassName

data class FileUnit(
    val packageName: String,
    override val declarations: List<Decl>,
) : DeclContainer

sealed interface DeclContainer {
    val declarations: List<Decl>
}

sealed interface Decl {
    val name: String
}

data class SortDecl(
    override val name: String,
    override val declarations: List<Decl>,
) : Decl, DeclContainer

data class RuleDecl(
    override val name: String,
    val symbols: List<Symbol>,
) : Decl

sealed interface Symbol

data class StringLitSymbol(
    val text: String,
) : Symbol

data class NamedSymbol(
    val name: String,
    val typeSpec: Type,
) : Symbol

sealed interface Type

data object IntType : Type

data object StringType : Type

data class RefType(
    val name: String,
) : Type

data class ResolvedRefType(
    val declType: ClassName,
//    val decl: Decl,
) : Type

data class StarType(
    val sortSpec: Type,
) : Type