package net.pelsmaeker.katerm

import io.kotest.core.spec.style.FunSpec
import net.pelsmaeker.katerm.terms.SimpleTermBuilder

/** Tests the [SimpleTermBuilder] class. */
class DefaultTermBuilderTests: FunSpec({

    include(testSimpleTermBuilder(::SimpleTermBuilder))

})