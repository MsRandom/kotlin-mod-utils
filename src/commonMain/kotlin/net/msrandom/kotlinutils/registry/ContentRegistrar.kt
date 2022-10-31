package net.msrandom.kotlinutils.registry

class ContentRegistrar<K, V : Any>(@JvmField internal val registry: RegistryWrapper<K, V>) {
    val values = mutableMapOf<RegistrySupplier<K, out V>, () -> V>()

    operator fun <T : V> set(key: K, valueSupplier: () -> T): RegistrySupplier<K, T> = SimpleRegistrySupplier<K, T>(registry, key).also {
        values[it] = valueSupplier
    }

    fun initialize() = registry.initialize(this)
}
