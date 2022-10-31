package net.msrandom.kotlinutils.registry

import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.Item
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.registry.VillagerRegistry
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.msrandom.kotlinutils.*
import net.msrandom.kotlinutils.WrappingBlockEntityType
import java.util.function.Function

internal val CONTAINER_REGISTRY = ExternalIdentityRegistry<ResourceLocation, MenuType<*>>()

object RegistryWrappers {
    @JvmField
    val BLOCKS: RegistryWrapper<ResourceLocation, Block> = RegistryWrapper.Forge(ForgeRegistries.BLOCKS)

    @JvmField
    val ITEMS: RegistryWrapper<ResourceLocation, Item> = RegistryWrapper.Forge(ForgeRegistries.ITEMS)

    @JvmField
    val BIOMES: RegistryWrapper<ResourceLocation, Biome> = RegistryWrapper.Forge(ForgeRegistries.BIOMES)

    @JvmField
    val POTION_EFFECTS: RegistryWrapper<ResourceLocation, MobEffect> = RegistryWrapper.Forge(ForgeRegistries.POTIONS)

    @JvmField
    val POTION_TYPES: RegistryWrapper<ResourceLocation, Potion> = RegistryWrapper.Forge(ForgeRegistries.POTION_TYPES)

    @JvmField
    val SOUND_EVENTS: RegistryWrapper<ResourceLocation, SoundEvent> = RegistryWrapper.Forge(ForgeRegistries.SOUND_EVENTS)

    @JvmField
    val ENCHANTMENTS: RegistryWrapper<ResourceLocation, Enchantment> = RegistryWrapper.Forge(ForgeRegistries.ENCHANTMENTS)

    @JvmField
    val VILLAGER_PROFESSIONS: RegistryWrapper<ResourceLocation, VillagerRegistry.VillagerProfession> = RegistryWrapper.VillagerProfessions

    @JvmField
    val TILE_ENTITIES: RegistryWrapper<ResourceLocation, BlockEntityType<*>> = RegistryWrapper.TileEntities

    @JvmField
    val CONTAINERS: RegistryWrapper<ResourceLocation, MenuType<*>> = RegistryWrapper.Containers
}

abstract class RegistryWrapper<K, V : Any> :
    (K?) -> V?,
    Function<K?, V?> {

    abstract operator fun get(key: K?): V?
    abstract fun getKey(value: V?): K?

    override fun invoke(key: K?) = get(key)
    override fun apply(key: K?) = get(key)

    internal abstract fun initialize(owner: ContentRegistrar<K, V>)

    open class Forge<T : IForgeRegistryEntry<T>>(@JvmField internal val forgeRegistry: IForgeRegistry<T>) :
        RegistryWrapper<ResourceLocation, T>() {

        private val registryFinalizer by lazy {
            val finalizer = ForgePlatformRegistryHandler.RegistryFinalizer(forgeRegistry)
            MinecraftForge.EVENT_BUS.register(finalizer)
            finalizer
        }

        override fun get(key: ResourceLocation?) = forgeRegistry.getValue(key)
        override fun getKey(value: T?) = forgeRegistry.getKey(value)

        override fun initialize(owner: ContentRegistrar<ResourceLocation, T>) {
            registryFinalizer.add(owner)
            MinecraftForge.EVENT_BUS.register(createHandler(owner))
        }

        protected open fun createHandler(owner: ContentRegistrar<ResourceLocation, T>) =
            ForgePlatformRegistryHandler(owner.values, forgeRegistry)
    }

    internal object VillagerProfessions : Forge<VillagerRegistry.VillagerProfession>(ForgeRegistries.VILLAGER_PROFESSIONS) {
        override fun createHandler(owner: ContentRegistrar<ResourceLocation, VillagerRegistry.VillagerProfession>) =
            object : ForgePlatformRegistryHandler<VillagerRegistry.VillagerProfession>(owner.values, ForgeRegistries.VILLAGER_PROFESSIONS) {
                override fun createName(name: ResourceLocation, value: VillagerRegistry.VillagerProfession) = value
            }
    }

    internal object TileEntities : RegistryWrapper<ResourceLocation, BlockEntityType<*>>() {
        private val blockRegistryFinalizer by lazy {
            val finalizer = BlockRegistryFinalizer()
            MinecraftForge.EVENT_BUS.register(finalizer)
            finalizer
        }

        override fun get(key: ResourceLocation?): BlockEntityType<*>? = BlockEntity.REGISTRY[key]?.let { WrappingBlockEntityType[it] }
        override fun getKey(value: BlockEntityType<*>?) = value?.entityClass?.let(BlockEntity.REGISTRY::getKey)

        override fun initialize(owner: ContentRegistrar<ResourceLocation, BlockEntityType<*>>) {
            blockRegistryFinalizer.add(owner)
            KotlinUtils.deferredRegistrars.add(owner)
        }

        private class BlockRegistryFinalizer {
            private val deferredRegistrars = hashSetOf<ContentRegistrar<ResourceLocation, BlockEntityType<*>>>()

            @SubscribeEvent(priority = EventPriority.LOWEST)
            fun register(@Suppress("UNUSED_PARAMETER") event: RegistryEvent.Register<Block>) {
                for (registrar in deferredRegistrars) {
                    for ((key, value) in registrar.values) {
                        GameRegistry.registerTileEntity(value().entityClass, key.registryKey)
                        key.update()
                    }
                }
            }

            fun add(registrar: ContentRegistrar<ResourceLocation, BlockEntityType<*>>) {
                deferredRegistrars.add(registrar)
            }
        }
    }

    internal object Containers : RegistryWrapper<ResourceLocation, MenuType<*>>() {
        private val handlers = hashSetOf<String>()

        override fun get(key: ResourceLocation?) = CONTAINER_REGISTRY[key]
        override fun getKey(value: MenuType<*>?) = CONTAINER_REGISTRY.getKey(value)

        override fun initialize(owner: ContentRegistrar<ResourceLocation, MenuType<*>>) {
            for ((key, value) in owner.values) {
                if (key.registryKey.namespace !in handlers) {
                    handlers.add(key.registryKey.namespace)

                    NetworkRegistry.INSTANCE.registerGuiHandler(key.registryKey.namespace, KotlinUtils.proxy)
                }

                CONTAINER_REGISTRY[key.registryKey] = value()
                key.update()
            }
        }
    }
}
