@file:Suppress("UNCHECKED_CAST", "TYPE_MISMATCH_WARNING", "UPPER_BOUND_VIOLATED_WARNING")

package net.msrandom.kotlinutils.registry

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.WritableRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.*
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty

actual fun <T : Any> ContentRegistrar(registry: Registry<T>, namespace: String): ContentRegistrar<T> =
    ContentRegistrar(RegistryCreationImpl.getActiveRegistry<T>(registry), namespace)

actual fun <T : Any> ContentRegistrar(registry: RegistryProvider<T>, namespace: String): ContentRegistrar<T> =
    object : ContentRegistrar<T> {
        val initializer by lazy(LazyThreadSafetyMode.NONE) {
            registry.forgeRegistry?.let { forgeRegistry ->
                object : ContentRegistrar<T> {
                    private val deferredRegister =
                        DeferredRegister.create(forgeRegistry, namespace) as DeferredRegister<*>

                    override val namespace get() = namespace

                    override operator fun <V : T> set(name: String, valueSupplier: () -> V) =
                        object : RegistrySupplier<V> {
                            private val delegate = RegistryCreationImpl.registerDeferred(deferredRegister, name, valueSupplier)

                            override val registryKey: ResourceLocation
                                get() = delegate.id

                            override fun get(): V = delegate.get() as V
                        }

                    override fun initialize() {
                        deferredRegister.register(FMLJavaModLoadingContext.get().modEventBus)
                    }
                }
            } ?: VanillaNamespacedInitializer(registry, namespace)
        }

        override val namespace: String
            get() = namespace

        override fun initialize() = initializer.initialize()

        override fun <V : T> set(name: String, valueSupplier: () -> V) = initializer.set(name, valueSupplier)
    }

actual interface RegistryProvider<T> : ReadOnlyProperty<Any?, Registry<T>>, () -> Registry<T>, Supplier<Registry<T>> {
    val forgeRegistry: IForgeRegistry<*>?
        get() = null
}

actual inline fun <reified T> createSimpleRegistry(id: ResourceLocation, uniqueType: Boolean): RegistryProvider<T> {
    return if (uniqueType) {
        RegistryCreationImpl.createForgeRegistry(id, T::class.java)
    } else {
        createVanillaRegistry(id)
    }
}

fun <T> createVanillaRegistry(id: ResourceLocation): RegistryProvider<T> {
    // Special handling for non-unique registry types, like Codec<T>s or function types, just return a vanilla registry to use(which is valid)
    val registry = MappedRegistry(ResourceKey.createRegistryKey<T>(id), Lifecycle.stable())

    (Registry.REGISTRY as WritableRegistry<Registry<*>>).register(
        registry.key() as ResourceKey<Registry<*>>,
        registry,
        Lifecycle.stable()
    )

    return RegistryProvider(registry)
}
