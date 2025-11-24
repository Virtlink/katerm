package net.pelsmaeker.katerm.generator

import io.kotest.core.spec.style.FunSpec
import net.pelsmaeker.katerm.generator.diagnostics.Message
import net.pelsmaeker.katerm.generator.diagnostics.MessageCollector
import java.nio.file.Path

class KatermCodeGeneratorTests: FunSpec({
    test("test") {
        // Arrange
        val program = """
            package net.pelsmaeker.l1lang.ast;

            sort HasDecls(
              imports: Import*,
              declarations: Decl*,
            );
            
            sort HasId(
              id: Id,
            );
            
            cons Unit(
              imports: Import*,
              declarations: Decl*,
              mainExpression: Expr,
            ) : HasDecls;
            
            template Unit = ${"\"\"\""}
              {% for import in imports %}
              {{ import }}
              {% endfor %}
        
              {% for declaration in declarations %}
              {{ declaration }}
              {% endfor %}
        
              return {{ mainExpression }};
            ${"\"\"\""};
            
            cons Import(
              id: Id,
            ) : HasId;
            
            template Import = "import {{ id }};";
            
            sort Decl(
              name: Name,
            );
            
            cons ModuleDecl(
              name: string,
              imports: Import*,
              declarations: Decl*,
            ) : Decl, HasDecls;
            
            template Module = ${"\"\"\""}
              mod {{ name }} {
                {% for import in imports %}
                {{ import }}
                {% endfor %}
        
                {% for declaration in declarations %}
                {{ declaration }}
                {% endfor %}
              }
            ${"\"\"\""};
            
            cons VarDecl(
              name: Name,
              type: Type,
              body: Expr,
            ) : Decl;
            
            template VarDecl = "var {{ name }}: {{ type }} = {{ body }};";
            
            sort Id;
            
            cons SimpleId(
              name: Name,
            ) : Id;
            
            template SimpleId = "{{ name }}";
            
            cons RootId(
              name: Name,
            ) : Id;
            
            template RootId = "::{{ name }}";
            
            cons QualifiedId(
              id: Id,
              name: Name,
            ) : Id;
            
            template QualifiedId = "{{ id }}::{{ name }}";
            
            cons Name(
              text: string,
            );
            
            template Name = "{{ text }}";
            
            sort Expr;
            
            cons IntLit(
              value: int,
            ) : Expr;
            
            template IntLit = "{{ value }}";
            
            cons Ref(
              id: Id,
            ) : Expr, HasId;
            
            template Ref = "{{ id }}";
            
            cons Assign(
              id: Id,
              expr: Expr,
            ) : Expr, HasId;
            
            template Assign = "{{ id }} := {{ expr }}";
            
            cons Add(
              left: Expr,
              right: Expr,
            ) : Expr;
            
            template Add = "({{ left }} + {{ right }})";
            
            cons Seq(
              first: Expr,
              second: Expr,
            ) : Expr;
            
            template Seq = "{{ first }}, {{ second }}";
            
            sort Type;
            
            cons IntType() : Type;
            
            template IntType = "Int";
        """.trimIndent()
        val messages = mutableListOf<Message>()
        val collector = MessageCollector { msg -> messages.add(msg); true }
        val parser = KatermParser(failFast = true)
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