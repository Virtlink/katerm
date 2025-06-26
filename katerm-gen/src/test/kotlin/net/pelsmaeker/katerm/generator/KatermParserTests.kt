package net.pelsmaeker.katerm.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.pelsmaeker.lsputils.diagnostics.Message
import net.pelsmaeker.lsputils.diagnostics.MessageCollector

class KatermParserTests: FunSpec({
    test("x") {
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

        // Act
        val parser = KatermParser()
        val result = parser.parse("test.katerm", program, collector)

        // Assert
        messages.isEmpty() shouldBe true
        result shouldNotBe null
        result!!
        print(result)
    }
})