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
            varSpecs = ctx.varSpecList().accept(varSpecListVisitor),
            types = ctx.typeList().accept(typeListVisitor),
        )

        override fun visitConsDecl(ctx: KatermAntlrParser.ConsDeclContext): ConsDecl = ConsDecl(
            name = ctx.ID().text,
            varSpecs = ctx.varSpecList().accept(varSpecListVisitor),
            types = ctx.typeList().accept(typeListVisitor),
        )

        override fun visitTemplateDecl(ctx: KatermAntlrParser.TemplateDeclContext): TemplateDecl = TemplateDecl(
            name = ctx.ID().text,
            templateText = ctx.STRINGLIT().text,
        )
    }

    private val varSpecListVisitor : KatermAntlrParserVisitor<List<VarSpec>> = object: KatermAntlrParserBaseVisitor<List<VarSpec>>() {
        override fun visitVarSpecList(ctx: KatermAntlrParser.VarSpecListContext): List<VarSpec> {
            val x = ctx.varSpec().accept(varSpecVisitor)
            val xs = ctx.varSpecList().accept(varSpecListVisitor)
            return listOf(x) + xs
        }
    }

    private val varSpecVisitor = object: KatermAntlrParserBaseVisitor<VarSpec>() {
        override fun visitVarSpec(ctx: KatermAntlrParser.VarSpecContext): VarSpec = VarSpec(
            name = ctx.ID().text,
            typeSpec = ctx.typeSpec().accept(typeSpecVisitor),
        )
    }

    private val typeSpecVisitor: KatermAntlrParserVisitor<TypeSpec> = object: KatermAntlrParserBaseVisitor<TypeSpec>() {
        override fun visitSimpleTypeSpec(ctx: KatermAntlrParser.SimpleTypeSpecContext): TypeSpec = SimpleTypeSpec(
            ctx.type().accept(typeVisitor)
        )

        override fun visitStarTypeSpec(ctx: KatermAntlrParser.StarTypeSpecContext): TypeSpec = StarTypeSpec(
            ctx.type().accept(typeVisitor)
        )

    }

    private val typeListVisitor : KatermAntlrParserVisitor<List<Type>> = object: KatermAntlrParserBaseVisitor<List<Type>>() {
        override fun visitTypeList(ctx: KatermAntlrParser.TypeListContext): List<Type> {
            val x = ctx.type().accept(typeVisitor)
            val xs = ctx.typeList().accept(this)
            return listOf(x) + xs
        }
    }

    private val typeVisitor: KatermAntlrParserVisitor<Type> = object: KatermAntlrParserBaseVisitor<Type>() {
        override fun visitSimpleTypeSpec(ctx: KatermAntlrParser.SimpleTypeSpecContext): Type =
            ctx.type().accept(typeVisitor)

        override fun visitRefType(ctx: KatermAntlrParser.RefTypeContext): Type = RefType(
            name = ctx.ID().text,
        )

        override fun visitIntType(ctx: KatermAntlrParser.IntTypeContext): Type = IntType

        override fun visitStringType(ctx: KatermAntlrParser.StringTypeContext): Type = StringType
    }

}