package net.pelsmaeker.katerm.substitutions

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.assume
import io.kotest.core.test.runIf

class EmptySubstitutionTests : FunSpec({
    include(testSubstitution { ps ->
        assume(ps.isEmpty())
        EmptySubstitution
    })
})

class SingletonSubstitutionTests : FunSpec({
    include(testSubstitution { ps ->
        assume(ps.size == 1)
        val (a, b) = ps.first()
        SingletonSubstitution(a, b)
    })
})