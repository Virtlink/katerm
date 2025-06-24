package net.pelsmaeker.katerm.generator.ast

import net.pelsmaeker.katerm.generator.KatermAntlrParser
import net.pelsmaeker.katerm.generator.KatermAntlrParserBaseVisitor
import net.pelsmaeker.katerm.generator.KatermAntlrParserVisitor

/**
 * Builds an AST from an ANTLR parse tree.
 */
class KatermAstBuilder {

    fun build(ctx: KatermAntlrParser.UnitContext): KatermUnit = ctx.accept(unitVisitor)

    private val unitVisitor = object: KatermAntlrParserBaseVisitor<KatermUnit>() {
        override fun visitUnit(ctx: KatermAntlrParser.UnitContext): KatermUnit = KatermUnit(
            languageName = ctx.ID().text,
            rules = ctx.rule_().map { it.accept(ruleVisitor) },
        )
    }

    private val ruleVisitor = object: KatermAntlrParserBaseVisitor<Rule>() {
        override fun visitRule(ctx: KatermAntlrParser.RuleContext): Rule {
            val (sort, name) = ctx.ruleName().accept(ruleNameVisitor)
            return Rule(
                sort = sort,
                name = name,
                symbols = ctx.ruleSymbol().map { it.accept(ruleSymbolVisitor) },
            )
        }
    }

    private val ruleNameVisitor = object: KatermAntlrParserBaseVisitor<Pair<String?, String>>() {
        override fun visitSimpleRuleName(ctx: KatermAntlrParser.SimpleRuleNameContext): Pair<String?, String> =
            Pair(null, ctx.ID().text)

        override fun visitQualifiedRuleName(ctx: KatermAntlrParser.QualifiedRuleNameContext): Pair<String?, String>? =
            Pair(ctx.ID(0).text, ctx.ID(1).text)
    }

    private val ruleSymbolVisitor = object: KatermAntlrParserBaseVisitor<Symbol>() {
        override fun visitLiteralSymbol(ctx: KatermAntlrParser.LiteralSymbolContext): Symbol = Symbol.StringLit(
            text = ctx.STRINGLIT().text.let { it.substring(1, it.length - 1) },
        )

        override fun visitNamedSymbol(ctx: KatermAntlrParser.NamedSymbolContext): Symbol = Symbol.Named(
            name = ctx.ID().text,
            typeSpec = ctx.typeSpec().accept(typeSpecVisitor)
        )
    }

    private val typeSpecVisitor: KatermAntlrParserVisitor<TypeSpec> = object: KatermAntlrParserBaseVisitor<TypeSpec>() {
        override fun visitSimpleTypeSpec(ctx: KatermAntlrParser.SimpleTypeSpecContext): TypeSpec =
            ctx.type().accept(typeSpecVisitor)

        override fun visitStarTypeSpec(ctx: KatermAntlrParser.StarTypeSpecContext): TypeSpec = TypeSpec.Star(
            sortSpec = ctx.type().accept(typeSpecVisitor),
        )

        override fun visitRefType(ctx: KatermAntlrParser.RefTypeContext): TypeSpec = TypeSpec.Ref(
            name = ctx.ID().text,
        )

        override fun visitIntType(ctx: KatermAntlrParser.IntTypeContext): TypeSpec = TypeSpec.Int

        override fun visitStringType(ctx: KatermAntlrParser.StringTypeContext): TypeSpec = TypeSpec.String
    }

}