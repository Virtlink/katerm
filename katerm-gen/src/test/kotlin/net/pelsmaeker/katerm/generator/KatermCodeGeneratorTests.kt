package net.pelsmaeker.katerm.generator

import io.kotest.core.spec.style.FunSpec
import net.pelsmaeker.pidxin.diagnostics.Message
import net.pelsmaeker.pidxin.diagnostics.MessageCollector
import net.pelsmaeker.pidxin.ResourceID
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
        val result = parser.parse(ResourceID.fromPath("test.katerm"), program, collector)
        val processedAst = KatermPreprocessor().preprocess(result!!)
        val generator = KatermCodeGenerator(
            outputDir = Path.of("tmp/"),
            ast = processedAst,
        )

        // Act
        generator.generateAll()
    }
})