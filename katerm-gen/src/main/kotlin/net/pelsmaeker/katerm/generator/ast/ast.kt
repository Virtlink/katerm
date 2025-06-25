package net.pelsmaeker.katerm.generator.ast

data class KatermUnit(
    val languageName: String,
    val rules: List<KatermRule>,
)

data class KatermRule(
    val sort: String?,
    val name: String,
    val symbols: List<KatermSymbol>,
)

sealed interface KatermSymbol {

    data class StringLit(
        val text: String,
    ) : KatermSymbol

    data class Named(
        val name: String,
        val typeSpec: KatermTypeSpec,
    ) : KatermSymbol

}

sealed interface KatermTypeSpec {

    data object Int : KatermTypeSpec

    data object String : KatermTypeSpec

    data class Ref(
        val name: kotlin.String,
    ) : KatermTypeSpec

    data class Star(
        val sortSpec: KatermTypeSpec,
    ) : KatermTypeSpec

}