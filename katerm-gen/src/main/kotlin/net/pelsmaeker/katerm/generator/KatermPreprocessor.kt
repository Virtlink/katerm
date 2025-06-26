package net.pelsmaeker.katerm.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import net.pelsmaeker.katerm.generator.ast.ConstructorClass
import net.pelsmaeker.katerm.generator.ast.Decl
import net.pelsmaeker.katerm.generator.ast.DeclContainer
import net.pelsmaeker.katerm.generator.ast.FileUnit
import net.pelsmaeker.katerm.generator.ast.IRDecl
import net.pelsmaeker.katerm.generator.ast.IRFileUnit
import net.pelsmaeker.katerm.generator.ast.IntType
import net.pelsmaeker.katerm.generator.ast.NamedSymbol
import net.pelsmaeker.katerm.generator.ast.ParameterDef
import net.pelsmaeker.katerm.generator.ast.RefType
import net.pelsmaeker.katerm.generator.ast.ResolvedRefType
import net.pelsmaeker.katerm.generator.ast.RuleDecl
import net.pelsmaeker.katerm.generator.ast.SortDecl
import net.pelsmaeker.katerm.generator.ast.SortInterface
import net.pelsmaeker.katerm.generator.ast.StarType
import net.pelsmaeker.katerm.generator.ast.StringType
import net.pelsmaeker.katerm.generator.ast.Symbol
import net.pelsmaeker.katerm.generator.ast.Type

class KatermPreprocessor {
    private val usedNames = mutableSetOf<String>()
    private val nameMapping = mutableMapOf<Decl, ClassName>()

    fun preprocess(ast: FileUnit): IRFileUnit {
        // First, for each sort and constructor declaration
        // we assign a unique class name.
        assignAllUniqueNames(ast.declarations, parents = listOf(ast), packageName = ast.packageName)

        // Next, we resolve each reference (RefType) to a sort or constructor declaration.
        // We take lexical scoping into account.
        val resolvedAst = resolveReferences(ast)

        // Next, we construct an intermediate representation (IR) of the Katerm file,
        // where we flatten the hierarchy of sorts and constructors,
        // find their class names, and assign appropriate parameter names to each constructor parameter.
        return buildIR(resolvedAst)
    }

    /**
     * Recursively assigns unique names to all declarations,
     *
     * @param decls The list of declarations to assign unique names to.
     * @param parents The list of parent sorts of the declarations in [decls], used to construct unique names.
     * @param packageName The name of the package into which the classes are generated.
     */
    private fun assignAllUniqueNames(decls: List<Decl>, parents: List<DeclContainer>, packageName: String) {
        for (decl in decls) {
            when (decl) {
                is SortDecl -> {
                    assignUniqueName(decl, parents, packageName)
                    assignAllUniqueNames(decl.declarations, parents + decl, packageName)
                }
                is RuleDecl -> assignUniqueName(decl, parents, packageName)
            }
        }
    }

    /**
     * Assigns a unique class name to the given declaration with the given parent sorts.
     *
     * @param decl The declaration to assign a unique name to.
     * @param parents The list of parent sorts.
     * @param packageName The name of the package into which the classes are generated.
     * @return The generated unique name for the declaration.
     */
    private fun assignUniqueName(decl: Decl, parents: List<DeclContainer>, packageName: String): String {
        val baseName = parents.joinToString(separator = "") { if (it is Decl) it.name else "" } + decl.name

        // Find a unique name for the declaration.
        var name = baseName
        var counter = 0
        while (name in usedNames) {
            counter += 1
            name = "$baseName$counter"
        }
        usedNames.add(name)
        nameMapping[decl] = ClassName(packageName, name)
        return name
    }

    /**
     * Resolves all references in the specified AST to their declarations.
     *
     * @param ast The AST in which to resolve references.
     * @return The new AST with resolved references.
     */
    private fun resolveReferences(ast: FileUnit): FileUnit {
        return FileUnit(
            packageName = ast.packageName,
            declarations = ast.declarations.map { resolveReferences(it, listOf(ast)) }
        )
    }

    /**
     * Resolves all references in the specified declaration to their declarations.
     *
     * @param decl The rule in which to resolve references.
     * @param parents The list of parent sorts of the rule, ordered from outermost to innermost.
     * @return The new declaration with resolved references.
     */
    private fun resolveReferences(decl: Decl, parents: List<DeclContainer>): Decl {
        val newDecl = when (decl) {
            is RuleDecl -> RuleDecl(
                name = decl.name,
                symbols = decl.symbols.map { resolveReferences(it, parents) }
            )
            is SortDecl -> SortDecl(
                name = decl.name,
                declarations = decl.declarations.map { resolveReferences(it, parents + decl) }
            )
        }
        nameMapping[newDecl] = nameMapping[decl]!!
        return newDecl
    }

    /**
     * Resolves all references in the specified symbol to their declarations.
     *
     * @param symbol The symbol in which to resolve references.
     * @param parents The list of parent sorts of the rule, ordered from outermost to innermost.
     * @return The new [Symbol] with resolved references.
     */
    private fun resolveReferences(symbol: Symbol, parents: List<DeclContainer>): Symbol = when (symbol) {
        is NamedSymbol -> NamedSymbol(symbol.name, resolveReferences(symbol.typeSpec, parents))
        else -> symbol
    }

    /**
     * Resolves all references in the specified type to their declarations.
     *
     * @param type The type in which to resolve references.
     * @param parents The list of parent sorts of the rule, ordered from outermost to innermost.
     * @return The new [Type] with resolved references.
     */
    private fun resolveReferences(type: Type, parents: List<DeclContainer>): Type = when (type) {
        is RefType -> resolveReferences(type, parents)
        is StarType -> StarType(resolveReferences(type.sortSpec, parents))
        else -> type
    }

    /**
     * Resolves the specified [RefType] to its declaration.
     *
     * @param type The [RefType] to resolve.
     * @param parents The list of parent sorts of the rule, ordered from outermost to innermost.
     * @return The [ResolvedRefType].
     */
    private fun resolveReferences(type: RefType, parents: List<DeclContainer>): ResolvedRefType {
        // Find the declaration that is referenced by going up the parent chain.
        val declarations = parents.asReversed().flatMap { it.declarations.map { d -> d.name to d } }
        val declaration = declarations.firstOrNull { it.first == type.name }?.second
            ?: throw IllegalArgumentException("Unknown type reference: ${type.name}")
        return ResolvedRefType(nameMapping[declaration]!!)
    }

    private fun buildIR(ast: FileUnit): IRFileUnit {
        return IRFileUnit(
            packageName = ast.packageName,
            decls = ast.declarations.flatMap { gatherIRDecls(it, null) },
        )
    }

    private fun gatherIRDecls(decl: Decl, parent: SortInterface?): List<IRDecl> = when (decl) {
        is SortDecl -> {
            val thisSort = SortInterface(
                nameMapping[decl]!!,
                parent,
                decl = decl,
            )
            val childSorts = decl.declarations.flatMap { gatherIRDecls(it, thisSort) }
            childSorts + thisSort
        }
        is RuleDecl -> {
            val namedSymbols = decl.symbols.filterIsInstance<NamedSymbol>()
            val thisConstructor = ConstructorClass(
                nameMapping[decl]!!,
                parent,
                parameters = namedSymbols.map { ns -> ParameterDef(ns.name, getTypeSpecType(ns.typeSpec)) },
                decl = decl,
            )
            listOf(thisConstructor)
        }
    }

    private fun getTypeSpecType(typeSpec: Type): TypeName = when (typeSpec) {
        is IntType -> Katerm.IntTerm
        is StringType -> Katerm.StringTerm
        is StarType -> Katerm.ListTerm(getTypeSpecType(typeSpec.sortSpec))
        is ResolvedRefType -> typeSpec.declType
        is RefType -> error("Unresolved reference: $typeSpec")
    }
}