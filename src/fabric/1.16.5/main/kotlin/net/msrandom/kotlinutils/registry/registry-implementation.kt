package net.msrandom.kotlinutils.registry

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty

actual interface RegistryProvider<T> : ReadOnlyProperty<Any?, Registry<T>>, () -> Registry<T>, Supplier<Registry<T>>

actual fun <T : Any> ContentRegistrar(registry: Registry<T>, namespace: String): ContentRegistrar<T> =
    ContentRegistrar<T>(RegistryProvider(registry), namespace)

actual fun <T : Any> ContentRegistrar(registry: RegistryProvider<T>, namespace: String): ContentRegistrar<T> =
    ContentRegistrar(registry, namespace)

actual inline fun <reified T> createSimpleRegistry(id: ResourceLocation, uniqueType: Boolean): RegistryProvider<T> =
    RegistryProvider(
        FabricRegistryBuilder.createSimple(
            T::class.java,
            id,
        ).buildAndRegister()
    )
