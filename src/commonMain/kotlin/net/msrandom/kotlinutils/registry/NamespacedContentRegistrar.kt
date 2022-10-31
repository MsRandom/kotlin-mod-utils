package net.msrandom.kotlinutils.registry

import net.minecraft.resources.ResourceLocation
import net.msrandom.kotlinutils.DelegateProvider

open class NamespacedContentRegistrar<T : Any>(private val registry: RegistryWrapper<ResourceLocation, T>, private val namespace: String) {
    val registrar = ContentRegistrar(registry)

    @JvmSynthetic
    fun <V : T> register(valueSupplier: () -> V) = DelegateProvider<RegistrySupplier<ResourceLocation, T>> {
        val supplier = NamespacedRegistrySupplier.fromName(registry, namespace, it)
        registrar.values[supplier] = valueSupplier
        supplier
    }

    fun <V : T> register(path: String, valueSupplier: () -> V): RegistrySupplier<ResourceLocation, T> {
        val supplier = NamespacedRegistrySupplier(registry, namespace, path)
        registrar.values[supplier] = valueSupplier
        return supplier
    }

    operator fun <V : T> set(name: String, valueSupplier: () -> V) =
        registrar.set(ResourceLocation(namespace, name), valueSupplier)

    fun initialize() = registrar.initialize()
}
