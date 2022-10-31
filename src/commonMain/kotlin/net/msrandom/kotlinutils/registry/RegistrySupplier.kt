package net.msrandom.kotlinutils.registry

import com.google.common.base.CaseFormat
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface RegistrySupplier<K, V> : ReadOnlyProperty<Any?, V>, Supplier<V>, () -> V {
    val registryKey: K

    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    override operator fun invoke() = get()

    fun update()
}

class SimpleRegistrySupplier<K, V : Any>(private val registry: RegistryWrapper<K, in V>, override val registryKey: K) : RegistrySupplier<K, V> {
    private lateinit var value: V

    override fun get() =
        if (::value.isInitialized) value else throw NullPointerException("${this::class.simpleName}::get called on $registryKey before it was registered")

    @Suppress("UNCHECKED_CAST")
    override fun update() {
        value = registry[registryKey] as? V ?: throw NullPointerException("$registryKey was not registered")
    }

    override fun toString() = registryKey.toString()
}

class NamespacedRegistrySupplier<V : Any>(
    private val registry: RegistryWrapper<ResourceLocation, in V>,
    namespace: String,
    path: String
) : RegistrySupplier<ResourceLocation, V> {
    private val key = ResourceLocation(namespace, path)
    private lateinit var value: V

    override val registryKey: ResourceLocation
        get() = key

    override fun get() =
        if (::value.isInitialized) value else throw NullPointerException("${this::class.simpleName}::get called on $registryKey before it was registered")

    @Suppress("UNCHECKED_CAST")
    override fun update() {
        value = registry[registryKey] as? V ?: throw NullPointerException("$registryKey was not registered")
    }

    override fun toString() = registryKey.toString()

    companion object {
        fun <V : Any> fromName(registry: RegistryWrapper<ResourceLocation, in V>, namespace: String, name: String) =
            NamespacedRegistrySupplier(registry, namespace, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name))
    }
}
