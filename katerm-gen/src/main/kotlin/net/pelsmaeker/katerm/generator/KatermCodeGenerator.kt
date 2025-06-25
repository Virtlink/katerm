package net.pelsmaeker.katerm.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import net.pelsmaeker.katerm.generator.ast.KatermUnit
import net.pelsmaeker.katerm.generator.ast.KatermRule
import net.pelsmaeker.katerm.generator.ast.KatermSymbol
import net.pelsmaeker.katerm.generator.ast.KatermTypeSpec
import java.nio.file.Path

class KatermCodeGenerator(
    private val ast: KatermUnit,
    private val outputDir: Path,
    private val packageName: String,
    private val classPrefix: String,
) {

    private val katermPackageName = "net.pelsmaeker.katerm.terms"
    private val Term = ClassName(katermPackageName, "Term")
    private val ApplTerm = ClassName(katermPackageName, "ApplTerm")
    private val IntTerm = ClassName(katermPackageName, "IntTerm")
    private val StringTerm = ClassName(katermPackageName, "StringTerm")
//    private val ListTerm = ParameterizedTypeName(katermPackageName, "ListTerm", )
    private val ListTerm = ClassName(katermPackageName, "ListTerm")
//    private val ListTerm = ClassName(katermPackageName, "ListTerm").parameterizedBy(TypeVariableName("R"))
    private val OptionTerm = ClassName(katermPackageName, "OptionTerm")
    private val ApplTermBase = ClassName(katermPackageName, "ApplTermBase")
    private val TermAttachments = ClassName(katermPackageName, "TermAttachments")
    private val TermVisitor = ClassName(katermPackageName, "TermVisitor")
    private val TermVisitor1 = ClassName(katermPackageName, "TermVisitor1")

    private val LangTerm = ClassName(packageName, "${classPrefix}Term")
    private val LangTermVisitor = ClassName(packageName, "${classPrefix}TermVisitor")
    private val LangTermVisitor1 = ClassName(packageName, "${classPrefix}TermVisitor1")

    private val sortTypes = ast.rules.mapNotNull { r -> r.sort?.let { it to r } }.associate { (s, r) ->
        s to ClassName(packageName, "${classPrefix}${s}Term")
    }

    private val applClasses = ast.rules.associate { r ->
        val cls = if (r.sort != null) {
            ClassName(packageName, "${classPrefix}${r.name}Term")
//            sortInterfaces[r.sort]!!.nestedClass(r.name)
        } else {
            ClassName(packageName, "${classPrefix}${r.name}Term")
        }
        r.name to cls
    }

    fun generateAll() {
        generateLangTerm()
        generateLangTermVisitor()
        generateLangTermVisitor1()

        for (sortName in sortTypes.keys) {
            generateSortTerm(sortName)
        }

        for (rule in ast.rules) {
            generateApplTerm(rule)
        }
    }

    fun generateLangTerm() {
        val file = FileSpec.builder(LangTerm).apply {
            addType(TypeSpec.interfaceBuilder(LangTerm).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.SEALED)
                addSuperinterface(Term)

                addFunction(FunSpec.builder("accept").apply {
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.ABSTRACT)
                    addTypeVariable(R)
                    addParameter("visitor", LangTermVisitor.parameterizedBy(R))
                    returns(R)
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val A = TypeVariableName("A")
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.ABSTRACT)
                    addTypeVariable(A)
                    addTypeVariable(R)
                    addParameter("visitor", LangTermVisitor1.parameterizedBy(A, R))
                    addParameter("arg", A)
                    returns(R)
                }.build())

            }.build())
        }.build()

        file.writeTo(outputDir)
    }

    fun generateLangTermVisitor() {
        val file = FileSpec.builder(LangTermVisitor).apply {
            addType(TypeSpec.interfaceBuilder(LangTermVisitor).apply {
                val R = TypeVariableName("R")

                addModifiers(KModifier.PUBLIC)
                addTypeVariable(TypeVariableName(R.name, variance = KModifier.OUT))

                for (rule in ast.rules) {
                    val applClass = applClasses[rule.name]!!

                    addFunction(FunSpec.builder("visit${applClass.simpleName}").apply {
                        addModifiers(KModifier.ABSTRACT)
                        addParameter(ParameterSpec.builder("term", applClass).build())
                        returns(R)
                    }.build())
                }

                addKdoc("""
                    Visitor for ${ast.languageName} terms.
                    
                    @param R The return type of the visitor methods.
                """.trimIndent())
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

    fun generateLangTermVisitor1() {
        val file = FileSpec.builder(LangTermVisitor1).apply {
            addType(TypeSpec.interfaceBuilder(LangTermVisitor1).apply {
                val A = TypeVariableName("A")
                val R = TypeVariableName("R")

                addModifiers(KModifier.PUBLIC)
                addTypeVariable(TypeVariableName(A.name, variance = KModifier.IN))
                addTypeVariable(TypeVariableName(R.name, variance = KModifier.OUT))

                for (rule in ast.rules) {
                    val applClass = applClasses[rule.name]!!

                    addFunction(FunSpec.builder("visit${applClass.simpleName}").apply {
                        addModifiers(KModifier.ABSTRACT)
                        addParameter(ParameterSpec.builder("term", applClass).build())
                        addParameter(ParameterSpec.builder("arg", A).build())
                        returns(R)
                    }.build())
                }

                addKdoc("""
                    Visitor for ${ast.languageName} terms.
                    
                    @param A The type of the argument passed to the visitor methods.
                    @param R The return type of the visitor methods.
                """.trimIndent())
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

    fun generateSortTerm(
        sortName: String,
    ) {
        val sortInterface = sortTypes[sortName] ?: error("No interface found for sort: $sortName")

        val file = FileSpec.builder(sortInterface).apply {
            addType(TypeSpec.interfaceBuilder(sortInterface).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.SEALED)
                addSuperinterface(LangTerm)
                addSuperinterface(Term)
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

    private fun getTypeSpecType(typeSpec: KatermTypeSpec): TypeName = when (typeSpec) {
        is KatermTypeSpec.Int -> IntTerm
        is KatermTypeSpec.String -> StringTerm
        is KatermTypeSpec.Ref -> sortTypes[typeSpec.name] ?: applClasses[typeSpec.name] ?: error("No interface/class found for sort/constructor: $typeSpec")
        is KatermTypeSpec.Star -> ListTerm.parameterizedBy(getTypeSpecType(typeSpec.sortSpec))
    }

    fun generateApplTerm(
        rule: KatermRule,
    ) {
        val applClass = applClasses[rule.name] ?: error("No class found for rule: ${rule.name}")

        val symbols = rule.symbols.filterIsInstance<KatermSymbol.Named>().associateWith { s ->
            getTypeSpecType(s.typeSpec)
        }

        val file = FileSpec.builder(applClass).apply {
            addType(TypeSpec.classBuilder(applClass).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.FINAL)
                if (rule.sort != null) {
                    addSuperinterface(sortTypes[rule.sort]!!)
                }
                addSuperinterface(LangTerm)
                addSuperinterface(Term)
                superclass(ApplTermBase)
                addSuperclassConstructorParameter("termAttachments")

                primaryConstructor(FunSpec.constructorBuilder().apply {
                    addModifiers(KModifier.INTERNAL)
                    for ((symbol, type) in symbols) {
                        addParameter(symbol.name, type)
                    }
                    addParameter("termAttachments", TermAttachments)
                }.build())

                addType(TypeSpec.companionObjectBuilder().apply {
                    addModifiers(KModifier.PUBLIC)
                    addProperty(PropertySpec.builder("OP", String::class).apply {
                        addModifiers(KModifier.CONST)
                        initializer("%S", rule.name)
                    }.build())
                    addProperty(PropertySpec.builder("ARITY", Int::class).apply {
                        addModifiers(KModifier.CONST)
                        initializer(symbols.size.toString())
                    }.build())
                }.build())

                symbols.onEachIndexed { index, pair ->
                    val (symbol, type) = pair

                    addProperty(PropertySpec.builder(symbol.name, type).apply {
                        initializer(symbol.name)
                    }.build())

                    addFunction(FunSpec.builder("component${index + 1}").apply {
                        addModifiers(KModifier.OVERRIDE)
                        addModifiers(KModifier.OPERATOR)
                        returns(type)
                        addStatement("return %N", symbol.name)
                    }.build())
                }

                addProperty(PropertySpec.builder("termOp", String::class).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("OP")
                }.build())

                addProperty(PropertySpec.builder("termArity", Int::class).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("ARITY")
                }.build())

                addProperty(PropertySpec.builder("termArgs", ListTerm.parameterizedBy(Term)).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer(
                        "listOf(%L)",
                        symbols.keys.joinToString(", ") { it.name }
                    )
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(R)
                    addParameter("visitor", TermVisitor.parameterizedBy(R))
                    returns(R)

                    addStatement("return visitor.visitAppl(this)")
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val A = TypeVariableName("A")
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(A)
                    addTypeVariable(R)
                    addParameter("visitor", TermVisitor1.parameterizedBy(A, R))
                    addParameter("arg", A)
                    returns(R)

                    addStatement("return visitor.visitAppl(this, arg)")
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(R)
                    addParameter("visitor", LangTermVisitor.parameterizedBy(R))
                    returns(R)

                    addStatement("return visitor.visit${applClass.simpleName}(this)")
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val A = TypeVariableName("A")
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(A)
                    addTypeVariable(R)
                    addParameter("visitor", LangTermVisitor1.parameterizedBy(A, R))
                    addParameter("arg", A)
                    returns(R)

                    addStatement("return visitor.visit${applClass.simpleName}(this, arg)")
                }.build())

                addFunction(FunSpec.builder("equalSubterms").apply {
                    addModifiers(KModifier.OVERRIDE)
                    addParameter("that", ApplTerm)
                    addParameter("compareAttachments", Boolean::class)
                    returns(Boolean::class)

                    addStatement("if (that !is %T) return false", applClass)
                    symbols.forEach { (symbol, _) ->
                        addStatement("if (!this.%N.equals(that.%N, compareAttachments = compareAttachments)) return false", symbol.name, symbol.name)
                    }
                    addStatement("return true")
                }.build())

                addProperty(PropertySpec.builder("subtermsHashCode", Int::class).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer(
                        "Objects.hash(%L)",
                        symbols.keys.joinToString(", ") { it.name }
                    )
                }.build())

            }.build())
        }.build()

        file.writeTo(outputDir)
    }

}