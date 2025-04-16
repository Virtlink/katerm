package net.pelsmaeker.katerm

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/** Tests the [SimpleTermBuilder] interface. */
fun testSimpleTermBuilder(
    create: () -> SimpleTermBuilder,
) = funSpec {

    context("newAppl()") {
        test("should create a new ApplTerm with the specified constructor and arguments") {
            // Arrange
            val builder = create()
            val constructor = "Foo"
            val arguments = listOf(
                builder.newString("a"),
                builder.newInt(1),
            )

            // Act
            val result = builder.newAppl(constructor, arguments)

            // Assert
            result.termOp shouldBe constructor
            result.termArgs shouldBe arguments
        }

        test("should accept an empty string as the constructor for a tuple") {
            // Arrange
            val builder = create()
            val constructor = ""
            val arguments = listOf(
                builder.newString("a"),
                builder.newInt(1),
            )

            // Act
            val result = builder.newAppl(constructor, arguments)

            // Assert
            result.termOp shouldBe constructor
            result.termArgs shouldBe arguments
        }

        test("should accept an empty argument list") {
            // Arrange
            val builder = create()
            val constructor = "Foo"
            val arguments = emptyList<Term>()

            // Act
            val result = builder.newAppl(constructor, arguments)

            // Assert
            result.termOp shouldBe constructor
            result.termArgs shouldBe arguments
        }

        test("should create an immutable term") {
            // Arrange
            val builder = create()
            val constructor = "Foo"
            val arguments = mutableListOf(
                builder.newString("a"),
                builder.newInt(1),
            )

            // Act
            val result = builder.newAppl(constructor, arguments)

            // Assert
            result.termArgs shouldBe arguments
            arguments.add(builder.newString("b"))
            result.termArgs shouldNotBe arguments
        }
    }

    context("newList()") {
        test("should create a new ListTerm with the specified elements") {
            // Arrange
            val builder = create()
            val elements = listOf(
                builder.newString("a"),
                builder.newInt(1),
            )

            // Act
            val result = builder.newList(elements)

            // Assert
            result.elements shouldBe elements
        }

        test("should accept an empty list") {
            // Arrange
            val builder = create()
            val elements = emptyList<Term>()

            // Act
            val result = builder.newList(elements)

            // Assert
            result.elements shouldBe elements
        }

        test("should create an immutable term") {
            // Arrange
            val builder = create()
            val elements = mutableListOf(
                builder.newString("a"),
                builder.newInt(1),
            )

            // Act
            val result = builder.newList(elements)

            // Assert
            result.elements shouldBe elements
            elements.add(builder.newString("b"))
            result.elements shouldNotBe elements
        }
    }

    context("newString()") {
        test("should create a new StringTerm with the specified value") {
            // Arrange
            val builder = create()
            val value = "a"

            // Act
            val result = builder.newString(value)

            // Assert
            result.termValue shouldBe value
        }
    }

    context("newInt()") {
        test("should create a new IntTerm with the specified value") {
            // Arrange
            val builder = create()
            val value = 1

            // Act
            val result = builder.newInt(value)

            // Assert
            result.termValue shouldBe value
        }
    }

    context("newReal()") {
        test("should create a new RealTerm with the specified value") {
            // Arrange
            val builder = create()
            val value = 1.0

            // Act
            val result = builder.newReal(value)

            // Assert
            result.termValue shouldBe value
        }
    }

    context("newVar()") {
        test("should create a new TermVar with the specified name") {
            // Arrange
            val builder = create()
            val name = "a"

            // Act
            val result = builder.newVar(name)

            // Assert
            result.name shouldBe name
        }
    }

}