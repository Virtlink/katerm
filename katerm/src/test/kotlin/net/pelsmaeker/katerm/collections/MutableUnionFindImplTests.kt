package net.pelsmaeker.katerm.collections

import io.kotest.core.spec.style.FunSpec

class MutableUnionFindImplTests : FunSpec({
    include(testDisjointSetTests { sets ->
        mutableDisjointSetOf(sets) as MutableUnionFindSetImpl<String>
    })
})