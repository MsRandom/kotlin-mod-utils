package net.msrandom.kotlinutils.registry

import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraftforge.registries.ObjectHolderRegistry

object RegistryWrappers {
    @JvmField
    val BLOCKS: RegistryWrapper<ResourceLocation, Block> = ForgeRegistryWrapper(ForgeRegistries.BLOCKS)

    @JvmField
    val ITEMS: RegistryWrapper<ResourceLocation, Item> = ForgeRegistryWrapper(ForgeRegistries.ITEMS)

    @JvmField
    val BIOMES: RegistryWrapper<ResourceLocation, Biome> = ForgeRegistryWrapper(ForgeRegistries.BIOMES)

    @JvmField
    val MOB_EFFECTS: RegistryWrapper<ResourceLocation, MobEffect> = ForgeRegistryWrapper(ForgeRegistries.POTIONS)

    @JvmField
    val POTIONS: RegistryWrapper<ResourceLocation, Potion> = ForgeRegistryWrapper(ForgeRegistries.POTION_TYPES)

    @JvmField
    val SOUND_EVENTS: RegistryWrapper<ResourceLocation, SoundEvent> = ForgeRegistryWrapper(ForgeRegistries.SOUND_EVENTS)

    @JvmField
    val ENCHANTMENTS: RegistryWrapper<ResourceLocation, Enchantment> = ForgeRegistryWrapper(ForgeRegistries.ENCHANTMENTS)

    @JvmField
    val VILLAGER_PROFESSIONS: RegistryWrapper<ResourceLocation, VillagerProfession> = ForgeRegistryWrapper(ForgeRegistries.PROFESSIONS)

    @JvmField
    val BLOCK_ENTITIES: RegistryWrapper<ResourceLocation, BlockEntityType<*>> = ForgeRegistryWrapper(ForgeRegistries.TILE_ENTITIES)

    @JvmField
    val MENUS: RegistryWrapper<ResourceLocation, MenuType<*>> = ForgeRegistryWrapper(ForgeRegistries.CONTAINERS)
}

open class ForgeRegistryWrapper<T : IForgeRegistryEntry<T>>(private val forgeRegistry: IForgeRegistry<T>) : RegistryWrapper<ResourceLocation, T>() {
    private val registrars = mutableListOf<ContentRegistrar<ResourceLocation, T>>()

    override fun get(key: ResourceLocation?) = forgeRegistry.getValue(key)
    override fun getKey(value: T?) = forgeRegistry.getKey(value)

    override fun initialize(owner: ContentRegistrar<ResourceLocation, T>) {
        for ((supplier) in owner.values) {
            ObjectHolderRegistry.addHandler {
                if (it.test(forgeRegistry.registryName)) supplier.update()
            }
        }
        registrars.add(owner)
        FMLJavaModLoadingContext.get().modEventBus.addGenericListener<RegistryEvent.Register<T>, T>(forgeRegistry.registrySuperType, EventPriority.HIGH) {
            for ((key, value) in owner.values.entries) {
                it.registry.register(value().setRegistryName(key.registryKey))
            }
        }
    }
}
