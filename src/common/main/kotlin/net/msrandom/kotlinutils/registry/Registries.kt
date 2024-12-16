package net.msrandom.kotlinutils.registry

import com.mojang.serialization.Codec
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

expect interface RegistryProvider<T> : ReadOnlyProperty<Any?, Registry<T>>, () -> Registry<T>, Supplier<Registry<T>>

fun <T> RegistryProvider(registry: Registry<T>) = object : RegistryProvider<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = registry
    override fun invoke() = registry
    override fun get() = registry
}

expect inline fun <reified T> createSimpleRegistry(id: ResourceLocation, uniqueType: Boolean = true): RegistryProvider<T>

expect fun <T> createCodecRegistry(id: ResourceLocation, codec: Codec<T>, networkCodec: Codec<T>? = codec): ResourceKey<Registry<T>>

operator fun <T> Registry<T>.set(key: ResourceLocation, value: T) {
    Registry.register(this, key, value)
}

fun <T> Registry<T>.getIdByKey(key: ResourceLocation) = getId(get(key))

fun <T> DefaultedRegistry<T>.getKeyOrNull(value: T): ResourceLocation? {
    val key = getKey(value)

    if (key != defaultKey) {
        return key
    }

    if (value == get(defaultKey)) {
        return defaultKey
    }

    return null
}

fun <T> Registry<T>.getKeyOrNull(value: T) = if (this is DefaultedRegistry<T>) {
    getKeyOrNull(value)
} else {
    getKey(value)
}
