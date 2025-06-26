package net.pelsmaeker.katerm.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import net.pelsmaeker.katerm.generator.ast.ConstructorClass
import net.pelsmaeker.katerm.generator.ast.IRFileUnit
import net.pelsmaeker.katerm.generator.ast.SortInterface
import java.nio.file.Path

class KatermCodeGenerator(
    private val ast: IRFileUnit,
//    private val ast: FileUnit,
    private val outputDir: Path,
//    private val packageName: String,
//    private val classPrefix: String,
) {

//    private val katermPackageName = "net.pelsmaeker.katerm.terms"
//    private val Term = ClassName(katermPackageName, "Term")
//    private val ApplTerm = ClassName(katermPackageName, "ApplTerm")
//    private val IntTerm = ClassName(katermPackageName, "IntTerm")
//    private val StringTerm = ClassName(katermPackageName, "StringTerm")
////    private val ListTerm = ParameterizedTypeName(katermPackageName, "ListTerm", )
//    private val ListTerm = ClassName(katermPackageName, "ListTerm")
////    private val ListTerm = ClassName(katermPackageName, "ListTerm").parameterizedBy(TypeVariableName("R"))
//    private val OptionTerm = ClassName(katermPackageName, "OptionTerm")
//    private val ApplTermBase = ClassName(katermPackageName, "ApplTermBase")
//    private val TermAttachments = ClassName(katermPackageName, "TermAttachments")
//    private val TermVisitor = ClassName(katermPackageName, "TermVisitor")
//    private val TermVisitor1 = ClassName(katermPackageName, "TermVisitor1")

    private val LangTerm = ClassName(ast.packageName, "Term") //"${classPrefix}Term")
    private val LangTermVisitor = ClassName(ast.packageName, "TermVisitor") //"${classPrefix}TermVisitor")
    private val LangTermVisitor1 = ClassName(ast.packageName, "TermVisitor1") //"${classPrefix}TermVisitor1")

//    private val sortTypes = ast.rules.mapNotNull { r -> r.sort?.let { it to r } }.associate { (s, r) ->
//        s to ClassName(packageName, "${classPrefix}${s}Term")
//    }
//
//    private val applClasses = ast.rules.associate { r ->
//        val cls = if (r.sort != null) {
//            ClassName(packageName, "${classPrefix}${r.name}Term")
////            sortInterfaces[r.sort]!!.nestedClass(r.name)
//        } else {
//            ClassName(packageName, "${classPrefix}${r.name}Term")
//        }
//        r.name to cls
//    }

    fun generateAll() {
        generateLangTerm()
        generateLangTermVisitor()
        generateLangTermVisitor1()

        for (sort in ast.sorts) {
            generateSortInterface(sort)
        }

        for (constructor in ast.constructors) {
            generateConstructorClass(constructor)
        }
    }

    fun generateLangTerm() {
        val file = FileSpec.builder(LangTerm).apply {
            addType(TypeSpec.interfaceBuilder(LangTerm).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.SEALED)
                addSuperinterface(Katerm.Term)

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

                for (constructor in ast.constructors) {
                    addFunction(FunSpec.builder("visit${constructor.type.simpleName}").apply {
                        addModifiers(KModifier.ABSTRACT)
                        addParameter(ParameterSpec.builder("term", constructor.type).build())
                        returns(R)
                    }.build())
                }

                addKdoc("""
                    Visitor for terms.
                    
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

                for (constructor in ast.constructors) {
                    addFunction(FunSpec.builder("visit${constructor.type.simpleName}").apply {
                        addModifiers(KModifier.ABSTRACT)
                        addParameter(ParameterSpec.builder("term", constructor.type).build())
                        addParameter(ParameterSpec.builder("arg", A).build())
                        returns(R)
                    }.build())
                }

                addKdoc("""
                    Visitor for terms.
                    
                    @param A The type of the argument passed to the visitor methods.
                    @param R The return type of the visitor methods.
                """.trimIndent())
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

    fun generateSortInterface(sort: SortInterface) {
        val file = FileSpec.builder(sort.type).apply {
            addType(TypeSpec.interfaceBuilder(sort.type).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.SEALED)
                if (sort.superSort != null) {
                    addSuperinterface(sort.superSort.type)
                }
                addSuperinterface(Katerm.Term)
            }.build())
        }.build()

        file.writeTo(outputDir)
    }

//    private fun getTypeSpecType(typeSpec: Type): TypeName = when (typeSpec) {
//        is Type.Int -> IntTerm
//        is Type.String -> StringTerm
//        is Type.Ref -> sortTypes[typeSpec.name] ?: applClasses[typeSpec.name] ?: error("No interface/class found for sort/constructor: $typeSpec")
//        is Type.Star -> ListTerm.parameterizedBy(getTypeSpecType(typeSpec.sortSpec))
//    }

    fun generateConstructorClass(
        constructor: ConstructorClass,
    ) {
//        val applClass = applClasses[rule.name] ?: error("No class found for rule: ${rule.name}")

//        val symbols = rule.symbols.filterIsInstance<Symbol.Named>().associateWith { s ->
//            getTypeSpecType(s.typeSpec)
//        }

        val file = FileSpec.builder(constructor.type).apply {
            addType(TypeSpec.classBuilder(constructor.type).apply {
                addModifiers(KModifier.PUBLIC)
                addModifiers(KModifier.FINAL)
                if (constructor.superSort != null) {
                    addSuperinterface(constructor.superSort.type)
                }
                addSuperinterface(Katerm.Term)
                superclass(Katerm.ApplTermBase)
                addSuperclassConstructorParameter("termAttachments")

                primaryConstructor(FunSpec.constructorBuilder().apply {
                    addModifiers(KModifier.INTERNAL)
                    for ((name, type) in constructor.parameters) {
                        addParameter(name, type)
                    }
                    addParameter("termAttachments", Katerm.TermAttachments)
                }.build())

                addType(TypeSpec.companionObjectBuilder().apply {
                    addModifiers(KModifier.PUBLIC)
                    addProperty(PropertySpec.builder("OP", String::class).apply {
                        addModifiers(KModifier.CONST)
                        initializer("%S", constructor.decl.name)
                    }.build())
                    addProperty(PropertySpec.builder("ARITY", Int::class).apply {
                        addModifiers(KModifier.CONST)
                        initializer(constructor.parameters.size.toString())
                    }.build())
                }.build())

                constructor.parameters.onEachIndexed { index, pair ->
                    val (name, type) = pair

                    addProperty(PropertySpec.builder(name, type).apply {
                        initializer(name)
                    }.build())

                    addFunction(FunSpec.builder("component${index + 1}").apply {
                        addModifiers(KModifier.OVERRIDE)
                        addModifiers(KModifier.OPERATOR)
                        returns(type)
                        addStatement("return %N", name)
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

                addProperty(PropertySpec.builder("termArgs", Katerm.ListTerm(Katerm.Term)).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer(
                        "listOf(%L)",
                        constructor.parameters.joinToString(", ") { it.name }
                    )
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(R)
                    addParameter("visitor", Katerm.TermVisitor(R))
                    returns(R)

                    addStatement("return visitor.visitAppl(this)")
                }.build())

                addFunction(FunSpec.builder("accept").apply {
                    val A = TypeVariableName("A")
                    val R = TypeVariableName("R")

                    addModifiers(KModifier.OVERRIDE)
                    addTypeVariable(A)
                    addTypeVariable(R)
                    addParameter("visitor", Katerm.TermVisitor1(A, R))
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

                    addStatement("return visitor.visit${constructor.type.simpleName}(this)")
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

                    addStatement("return visitor.visit${constructor.type.simpleName}(this, arg)")
                }.build())

                addFunction(FunSpec.builder("equalSubterms").apply {
                    addModifiers(KModifier.OVERRIDE)
                    addParameter("that", Katerm.ApplTerm)
                    addParameter("compareAttachments", Boolean::class)
                    returns(Boolean::class)

                    addStatement("if (that !is %T) return false", constructor.type)
                    constructor.parameters.forEach { (name, _) ->
                        addStatement("if (!this.%N.equals(that.%N, compareAttachments = compareAttachments)) return false", name, name)
                    }
                    addStatement("return true")
                }.build())

                addProperty(PropertySpec.builder("subtermsHashCode", Int::class).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer(
                        "Objects.hash(%L)",
                        constructor.parameters.joinToString(", ") { it.name }
                    )
                }.build())

            }.build())
        }.build()

        file.writeTo(outputDir)
    }

}