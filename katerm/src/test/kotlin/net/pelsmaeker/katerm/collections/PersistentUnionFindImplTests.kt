package net.pelsmaeker.katerm.collections

import io.kotest.core.spec.style.FunSpec

class PersistentUnionFindImplTests : FunSpec({
    include(testDisjointSetsTests { sets ->
        persistentDisjointSetsOf(sets) as PersistentUnionFindImpl<String>
    })
})