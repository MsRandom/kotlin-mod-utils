package net.msrandom.kotlinutils.registry

import java.util.function.Function

abstract class RegistryWrapper<K, V : Any> : (K?) -> V?, Function<K?, V?> {
    abstract operator fun get(key: K?): V?
    abstract fun getKey(value: V?): K?

    override fun invoke(key: K?) = get(key)
    override fun apply(key: K?) = get(key)

    internal abstract fun initialize(owner: ContentRegistrar<K, V>)
}
