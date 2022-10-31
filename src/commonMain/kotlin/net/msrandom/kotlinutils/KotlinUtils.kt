package net.msrandom.kotlinutils

import net.msrandom.kotlinutils.registry.ContentRegistrar

class KotlinUtils {
    companion object {
        const val MOD_ID = "kotlinutils"
        internal val deferredRegistrars = hashSetOf<ContentRegistrar<*, *>>()
    }
}
