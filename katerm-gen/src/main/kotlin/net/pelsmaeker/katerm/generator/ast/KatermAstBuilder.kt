package net.pelsmaeker.katerm.generator.ast

import net.pelsmaeker.katerm.generator.KatermAntlrParser
import net.pelsmaeker.katerm.generator.KatermAntlrParserBaseVisitor
import net.pelsmaeker.katerm.generator.KatermAntlrParserVisitor

/**
 * Builds an AST from an ANTLR parse tree.
 */
class KatermAstBuilder {

    fun build(ctx: KatermAntlrParser.UnitContext): FileUnit = ctx.accept(unitVisitor)

    private val unitVisitor : KatermAntlrParserVisitor<FileUnit> = object: KatermAntlrParserBaseVisitor<FileUnit>() {
        override fun visitUnit(ctx: KatermAntlrParser.UnitContext): FileUnit = FileUnit(
            packageName = ctx.QID().text,
            declarations = ctx.decl().map { it.accept(declVisitor) },
        )
    }

    private val declVisitor : KatermAntlrParserVisitor<Decl> = object: KatermAntlrParserBaseVisitor<Decl>() {
        override fun visitSortDecl(ctx: KatermAntlrParser.SortDeclContext): SortDecl = SortDecl(
            name = ctx.ID().text,
            declarations = ctx.decl().map { it.accept(declVisitor) },
        )

        override fun visitRuleDecl(ctx: KatermAntlrParser.RuleDeclContext): RuleDecl = RuleDecl(
            name = ctx.ID().text,
            symbols = ctx.ruleSymbol().map { it.accept(ruleSymbolVisitor) },
        )
    }

    private val ruleSymbolVisitor = object: KatermAntlrParserBaseVisitor<Symbol>() {
        override fun visitLiteralSymbol(ctx: KatermAntlrParser.LiteralSymbolContext): Symbol = StringLitSymbol(
            text = ctx.STRINGLIT().text.let { it.substring(1, it.length - 1) },
        )

        override fun visitNamedSymbol(ctx: KatermAntlrParser.NamedSymbolContext): Symbol = NamedSymbol(
            name = ctx.ID().text,
            typeSpec = ctx.typeSpec().accept(typeSpecVisitor)
        )
    }

    private val typeSpecVisitor: KatermAntlrParserVisitor<Type> = object: KatermAntlrParserBaseVisitor<Type>() {
        override fun visitSimpleTypeSpec(ctx: KatermAntlrParser.SimpleTypeSpecContext): Type =
            ctx.type().accept(typeSpecVisitor)

        override fun visitStarTypeSpec(ctx: KatermAntlrParser.StarTypeSpecContext): Type = StarType(
            sortSpec = ctx.type().accept(typeSpecVisitor),
        )

        override fun visitRefType(ctx: KatermAntlrParser.RefTypeContext): Type = RefType(
            name = ctx.ID().text,
        )

        override fun visitIntType(ctx: KatermAntlrParser.IntTypeContext): Type = IntType

        override fun visitStringType(ctx: KatermAntlrParser.StringTypeContext): Type = StringType
    }

}