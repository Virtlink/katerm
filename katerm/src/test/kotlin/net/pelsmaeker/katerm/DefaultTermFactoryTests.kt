package net.pelsmaeker.katerm

import io.kotest.core.spec.style.FunSpec

/** Tests the [DefaultSimpleTermBuilder] class. */
class DefaultTermBuilderTests: FunSpec({

    include(testSimpleTermBuilder(::DefaultSimpleTermBuilder))

})