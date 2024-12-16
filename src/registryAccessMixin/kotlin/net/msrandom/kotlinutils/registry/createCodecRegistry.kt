package net.msrandom.kotlinutils.registry

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess.RegistryData
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

internal val codecRegistries = hashMapOf<ResourceKey<out Registry<*>>, RegistryData<*>>()

actual fun <T> createCodecRegistry(id: ResourceLocation, codec: Codec<T>, networkCodec: Codec<T>?): ResourceKey<Registry<T>> {
    val key = ResourceKey.createRegistryKey<T>(
        ResourceLocation(id.namespace, "${id.namespace}.${id.path}")
    )

    codecRegistries[key] = RegistryData(key, codec, networkCodec)

    return key
}

@Suppress("UNCHECKED_CAST")
fun <T> codec(key: ResourceKey<Registry<T>>) =
    codecRegistries.getValue(key).codec() as Codec<T>
