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
            package net.pelsmaeker.joelang.ast;

            Unit = <modules: Module*>;
            
            Module = "mod" <name: string> "{" <declarations: Decl*> "}";
            
            sort Decl {
              Def = "def" <name: string> ":" <type: Type> "=" <body: Expr> ";" ;
            }
            
            sort Type {
              Int = "int";
            }
            
            sort Expr {
              IntLiteral = <value: int>;
              Ref = <name: string>;
            }
            
            sort AnalyzerType {
              INT = "INT";
              ERROR = "ERROR";
            }
            
            sort Label {
              MOD = "MOD" "(" <name: string> ")";
              DEF = <name: string> ":" <type: AnalyzerType>;
              LEX = "LEX";
            }
        """.trimIndent()
        val messages = mutableListOf<Message>()
        val collector = MessageCollector { msg -> messages.add(msg); true }
        val parser = KatermParser()
        val result = parser.parse("test.katerm", program, collector)
        val processedAst = KatermPreprocessor().preprocess(result!!)
        val generator = KatermCodeGenerator(
            outputDir = Path.of("/Users/daniel/git/virtlink/katerm/tmp"),
            ast = processedAst,
        )

        // Act
        generator.generateAll()
    }
})