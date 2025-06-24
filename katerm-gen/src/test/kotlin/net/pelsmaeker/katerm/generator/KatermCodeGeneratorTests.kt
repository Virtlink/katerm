package net.pelsmaeker.katerm.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.pelsmaeker.lsputils.diagnostics.Message
import net.pelsmaeker.lsputils.diagnostics.MessageCollector
import java.nio.file.Path

class KatermCodeGeneratorTests: FunSpec({
    test("y") {
        // Arrange
        val program = """
            language Joe;

            Unit = <modules: Module*>;
            
            Module = "mod" <name: string> "{" <declarations: Decl*> "}";
            
            Decl.Def = "def" <name: string> ":" <type: Type> "=" <body: Expr> ";" ;
            
            Type.Int = "int";
            
            Expr.IntLiteral = <value: int>;
            Expr.Ref = <name: string>;
            
            
            AnalyzerType.INT = "INT";
            AnalyzerType.ERROR = "ERROR";
            
            Label.MOD = "MOD" "(" <name: string> ")";
            Label.DEF = <name: string> ":" <type: AnalyzerType>;
            Label.LEX = "LEX";
        """.trimIndent()
        val messages = mutableListOf<Message>()
        val collector = MessageCollector { msg -> messages.add(msg); true }
        val parser = KatermParser()
        val result = parser.parse("test.katerm", program, collector)
        val generator = KatermCodeGenerator(
            outputDir = Path.of("/Users/daniel/git/virtlink/katerm/tmp"),
            classPrefix = "Joe",
        )

        // Act
        generator.generateSort(
            ast = result!!,
            sortName = "Unit",
        )
    }
})