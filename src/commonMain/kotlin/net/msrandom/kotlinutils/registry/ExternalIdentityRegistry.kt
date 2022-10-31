package net.msrandom.kotlinutils.registry

import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap

// Add network IDs to the regular registry, along with null handling for the IDs.
open class ExternalIdentityRegistry<K, V : Any> @JvmOverloads constructor(initialSize: Int = 16) : ExternalRegistry<K, V>(initialSize) {
    @Suppress("MemberVisibilityCanBePrivate")
    protected val idRegistry = CrudeIncrementalIntIdentityHashBiMap<V>(initialSize)

    open fun getIdByKey(key: K?) = getId(this[key])
    open fun getId(value: V?) = if (value == null) -1 else idRegistry.getId(value)
    open fun getKey(id: Int) = getKey(get(id))
    open operator fun get(id: Int) = if (id == -1) null else idRegistry.byId(id)

    override operator fun set(key: K, value: V) {
        super.set(key, value)
        idRegistry.add(value)
    }
}
