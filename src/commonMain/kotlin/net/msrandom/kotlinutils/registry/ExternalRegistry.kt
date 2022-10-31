package net.msrandom.kotlinutils.registry

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import net.msrandom.kotlinutils.KotlinUtils
import java.util.*

open class ExternalRegistry<K, V : Any> @JvmOverloads constructor(initialSize: Int = 16) : RegistryWrapper<K, V>(), Iterable<Map.Entry<K, V>> {
    protected val registry: BiMap<K, V> = HashBiMap.create<K, V>(initialSize)

    open val size
        get() = registry.size

    open operator fun contains(key: K?) = key != null && key in registry
    override operator fun get(key: K?) = if (key == null) null else registry[key]
    override fun getKey(value: V?) = if (value == null) null else registry.inverse()[value]

    open fun getRandom(random: Random): K? =
        if (size > 0) registry.keys.elementAt(random.nextInt(registry.size)) else null

    open operator fun set(key: K, value: V) {
        registry[key] = value
    }

    override fun iterator(): Iterator<Map.Entry<K, V>> = registry.iterator()

    override fun initialize(owner: ContentRegistrar<K, V>) {
        for ((key, value) in owner.values) {
            this[key.registryKey] = value()
            key.update()
        }

        KotlinUtils.deferredRegistrars.add(owner)
    }
}
