package net.msrandom.kotlinutils.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub

interface ContentRegistrar<T : Any> {
    val namespace: String

    operator fun <V : T> set(name: String, valueSupplier: () -> V): RegistrySupplier<V>
    fun initialize()
}

@Stub
internal expect fun ResourceLocation(namespace: String, path: String): ResourceLocation

@Stub
expect fun <T : Any> ContentRegistrar(registry: Registry<T>, namespace: String): ContentRegistrar<T>

@Stub
expect fun <T : Any> ContentRegistrar(registry: RegistryProvider<T>, namespace: String): ContentRegistrar<T>

class SimpleRegistrySupplier<T : Any, V : T>(private val registry: RegistryProvider<T>, override val registryKey: ResourceLocation) :
    RegistrySupplier<V> {
    private lateinit var value: V

    override fun get() = if (::value.isInitialized) value else throw NullPointerException("${this::class.simpleName}::get called on $registryKey before it was registered")

    fun update() {
        @Suppress("UNCHECKED_CAST")
        value = registry.get()[registryKey] as? V ?: throw NullPointerException("$registryKey was not registered")
    }

    override fun toString() = registryKey.toString()
}

class VanillaNamespacedInitializer<T : Any>(private val registry: RegistryProvider<T>, override val namespace: String) :
    ContentRegistrar<T> {
    private val values = mutableListOf<Pair<SimpleRegistrySupplier<T, out T>, () -> T>>()

    override operator fun <V : T> set(name: String, valueSupplier: () -> V): RegistrySupplier<V> = SimpleRegistrySupplier<T, V>(registry, ResourceLocation(namespace, name)).also {
        values.add(it to valueSupplier)
    }

    override fun initialize() {
        val registry = registry()

        for ((supplier, factory) in values) {
            registry[supplier.registryKey] = factory()
            supplier.update()
        }
    }
}
