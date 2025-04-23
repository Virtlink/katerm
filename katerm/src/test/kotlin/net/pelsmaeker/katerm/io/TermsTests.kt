package net.pelsmaeker.katerm.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import net.pelsmaeker.katerm.DefaultSimpleTermBuilder
import net.pelsmaeker.katerm.Term
import net.pelsmaeker.katerm.TermAttachments

@Suppress("UNCHECKED_CAST")
object ListOfStringAnnotationKey: TermAttachments.Key<List<String>>(List::class.java as Class<out List<String>>)

/** Tests an implementation of the [Term] interface. */
@Suppress("TestFunctionName")
fun TermTests(constructor: (attachments: TermAttachments) -> Term) = funSpec {

    context("termAttachments") {
        test("should return the term attachments") {
            // Arrange
            val attachments = TermAttachments.of(ListOfStringAnnotationKey to listOf("A", "B"))

            // Act
            val term = constructor(attachments)

            // Assert
            term.termAttachments shouldBe attachments
        }
    }

}

class DefaultTermBuilderTests: FunSpec({

    include(TermTests { a -> DefaultSimpleTermBuilder().newAppl("MyCons", emptyList(), a) })

})