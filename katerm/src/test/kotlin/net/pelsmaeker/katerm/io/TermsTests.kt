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
fun TermTests(constructor: (attachments: TermAttachments, separators: List<String>?) -> Term) = funSpec {

    context("termAttachments") {
        test("should return the term attachments") {
            // Arrange
            val attachments = TermAttachments.of(ListOfStringAnnotationKey to listOf("A", "B"))

            // Act
            val term = constructor(attachments, null)

            // Assert
            term.termAttachments shouldBe attachments
        }
    }

    context("termSeparators") {
        xtest("should return the term separators") {
            // Arrange
            val separators = listOf("A", "B")
            // FIXME: This won't work since we don't know how many separators the term expects

            // Act
            val term = constructor(TermAttachments.empty(), separators)

            // Assert
            term.termSeparators shouldBe separators
        }
    }

}

class DefaultTermBuilderTests: FunSpec({

    include(TermTests { a, s -> DefaultSimpleTermBuilder().newAppl("MyCons", emptyList(), a, s) })

})