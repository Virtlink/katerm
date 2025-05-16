package net.pelsmaeker.katerm.collections

import io.kotest.core.spec.style.FunSpec

class MutableUnionFindImplTests : FunSpec({
    include(testDisjointSetsTests { sets ->
        mutableDisjointSetsOf(sets) as MutableUnionFindImpl<String>
    })
})