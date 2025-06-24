package net.pelsmaeker.katerm.generator.ast

interface KatermAST

data class KatermUnit(
    val languageName: String,
    val rules: List<Rule>,
)

data class Rule(
    val sort: String?,
    val name: String,
    val symbols: List<Symbol>,
)

interface Symbol {

    data class StringLit(
        val text: String,
    ) : Symbol

    data class Named(
        val name: String,
        val typeSpec: TypeSpec,
    ) : Symbol

}

interface TypeSpec {

    data object Int : TypeSpec

    data object String : TypeSpec

    data class Ref(
        val name: kotlin.String,
    ) : TypeSpec

    data class Star(
        val sortSpec: TypeSpec,
    ) : TypeSpec

}