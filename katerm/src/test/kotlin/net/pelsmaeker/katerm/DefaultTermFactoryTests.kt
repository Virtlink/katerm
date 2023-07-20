package net.pelsmaeker.katerm

import io.kotest.core.spec.style.FunSpec

/** Tests the [DefaultTermBuilder] class. */
class DefaultTermBuilderTests: FunSpec({

    include(termBuilderTests(::DefaultTermBuilder))

})