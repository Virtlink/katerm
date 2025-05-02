package net.pelsmaeker.katerm

import io.kotest.core.spec.style.FunSpec

/** Tests the [SimpleTermBuilder] class. */
class DefaultTermBuilderTests: FunSpec({

    include(testSimpleTermBuilder(::SimpleTermBuilder))

})