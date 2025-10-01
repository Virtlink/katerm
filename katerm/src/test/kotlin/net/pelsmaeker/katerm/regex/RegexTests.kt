package net.pelsmaeker.katerm.regex

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.pelsmaeker.katerm.substitutions.Substitution
import net.pelsmaeker.katerm.substitutions.emptySubstitution
import net.pelsmaeker.katerm.terms.Term
import net.pelsmaeker.katerm.terms.TermVar
import net.pelsmaeker.katerm.testRegexBuilder

typealias SubstMap = Map<Set<TermVar>, Term>

class RegexTests : FunSpec({

    withData<Pair<Pair<Regex<Term, Substitution>, List<Term>>, SubstMap?>>(
        nameFn = { (rt: Pair<Regex<Term, Substitution>, List<Term>>, s: SubstMap?) -> "should ${if (s == null) "not" else ""} derive and unify ${rt.first} when applied to ${rt.second}" },
        with(testRegexBuilder) { listOf<Pair<Pair<Regex<Term, Substitution>, List<Term>>, SubstMap?>>(
            // Atom
            Pair(
                // Foo
                T(string("Foo")),
                // Foo
                listOf(string("Foo"))
            ) to emptyMap(),
            Pair(
                // Foo · Bar
                T(string("Foo")) .. T(string("Bar")),
                // Foo Bar
                listOf(string("Foo"), string("Bar"))
            ) to emptyMap(),
            // Concat
            Pair(
                // Foo · Bar
                T(string("Foo")) .. T(string("Bar")),
                // Qux Bar
                listOf(string("Qux"), string("Bar"))
            ) to null,
            Pair(
                // Foo · Bar
                T(string("Foo")) .. T(string("Bar")),
                // Foo Qux
                listOf(string("Foo"), string("Qux"))
            ) to null,
//            // And
//            Pair(
//                // Foo & Bar
//                T(string("Foo")) and T(string("Bar")),
//                // Foo
//                listOf(string("Foo"))
//            ) to null,
//            Pair(
//                // Foo & Foo
//                T(string("Foo")) and T(string("Foo")),
//                // Foo
//                listOf(string("Foo"))
//            ) to emptyMap(),
//            Pair(
//                // Foo & Foo
//                T(string("Foo")) and T(string("Foo")),
//                // Qux
//                listOf(string("Qux"))
//            ) to null,
            // Or
            Pair(
                // Foo | Bar
                T(string("Foo")) or T(string("Bar")),
                // Foo
                listOf(string("Foo"))
            ) to emptyMap(),
            Pair(
                // Foo | Bar
                T(string("Foo")) or T(string("Bar")),
                // Bar
                listOf(string("Bar"))
            ) to emptyMap(),
            Pair(
                // Foo | Bar
                T(string("Foo")) or T(string("Bar")),
                // Qux
                listOf(string("Qux"))
            ) to null,
            // KleeneStar
            Pair(
                // Foo*
                T(string("Foo")).`*`,
                // []
                emptyList<Term>()
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*`,
                // Foo
                listOf(string("Foo"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*`,
                // Foo Foo
                listOf(string("Foo"), string("Foo"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*`,
                // Foo Foo Foo
                listOf(string("Foo"), string("Foo"), string("Foo"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*`,
                // Bar
                listOf(string("Bar"))
            ) to null,
            // KleeneStar 2
            Pair(
                // Foo*
                T(string("Foo")).`*` .. T(string("Bar")),
                // []
                listOf<Term>(string("Bar"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*` .. T(string("Bar")),
                // Foo
                listOf(string("Foo"), string("Bar"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*` .. T(string("Bar")),
                // Foo Foo
                listOf(string("Foo"), string("Foo"), string("Bar"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*` .. T(string("Bar")),
                // Foo Foo Foo
                listOf(string("Foo"), string("Foo"), string("Foo"), string("Bar"))
            ) to emptyMap(),
            Pair(
                // Foo*
                T(string("Foo")).`*` .. T(string("Bar")),
                // Bar
                listOf(string("Bar"))
            ) to emptyMap(),
        ) }
    ) { (regexTerms: Pair<Regex<Term, Substitution>, List<Term>>, expectedSubstitution: SubstMap?) ->
        val (regex: Regex<Term, Substitution>, terms: List<Term>) = regexTerms
        with(testRegexBuilder) {
            // Act
            var m = regex.buildMatcher(emptySubstitution())
            for (term in terms) {
                m = m.match(term)
            }

            if (expectedSubstitution == null) {
                m.isEmpty() shouldBe true
            } else {
                m.isEmpty() shouldNotBe true
                val newSubstitution = m.getAcceptingMetadata() ?: emptySubstitution()
                newSubstitution.toMap() shouldBe expectedSubstitution
            }
        }
    }

})