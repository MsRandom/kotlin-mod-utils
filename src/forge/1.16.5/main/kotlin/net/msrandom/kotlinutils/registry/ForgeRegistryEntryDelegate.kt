package net.msrandom.kotlinutils.registry

import com.google.common.reflect.TypeToken
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryEntry

@Suppress("UNCHECKED_CAST")
open class ForgeRegistryEntryDelegate<T : Any> : IForgeRegistryEntry<T> {
    private val token = object : TypeToken<T>(javaClass) {}

    private var registryName: ResourceLocation? = null

    override fun setRegistryName(resourceLocation: ResourceLocation): T {
        require(registryName == null)

        registryName = resourceLocation

        return this as T
    }

    override fun getRegistryName() = registryName

    override fun getRegistryType() = token.rawType as Class<T>
}
