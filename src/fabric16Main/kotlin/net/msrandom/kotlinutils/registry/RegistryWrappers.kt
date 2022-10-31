package net.msrandom.kotlinutils.registry

import net.minecraft.core.Registry
import net.minecraft.data.BuiltinRegistries
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
import java.util.function.Function

object RegistryWrappers {
    @JvmField
    val BLOCKS: RegistryWrapper<ResourceLocation, Block> = VanillaRegistryWrapper(Registry.BLOCK)

    @JvmField
    val ITEMS: RegistryWrapper<ResourceLocation, Item> = VanillaRegistryWrapper(Registry.ITEM)

    @JvmField
    val BIOMES: RegistryWrapper<ResourceLocation, Biome> = VanillaRegistryWrapper(BuiltinRegistries.BIOME)

    @JvmField
    val MOB_EFFECTS: RegistryWrapper<ResourceLocation, MobEffect> = VanillaRegistryWrapper(Registry.MOB_EFFECT)

    @JvmField
    val POTIONS: RegistryWrapper<ResourceLocation, Potion> = VanillaRegistryWrapper(Registry.POTION)

    @JvmField
    val SOUND_EVENTS: RegistryWrapper<ResourceLocation, SoundEvent> = VanillaRegistryWrapper(Registry.SOUND_EVENT)

    @JvmField
    val ENCHANTMENTS: RegistryWrapper<ResourceLocation, Enchantment> = VanillaRegistryWrapper(Registry.ENCHANTMENT)

    @JvmField
    val VILLAGER_PROFESSIONS: RegistryWrapper<ResourceLocation, VillagerProfession> = VanillaRegistryWrapper(Registry.VILLAGER_PROFESSION)

    @JvmField
    val BLOCK_ENTITIES: RegistryWrapper<ResourceLocation, BlockEntityType<*>> = VanillaRegistryWrapper(Registry.BLOCK_ENTITY_TYPE)

    @JvmField
    val MENUS: RegistryWrapper<ResourceLocation, MenuType<*>> = VanillaRegistryWrapper(Registry.MENU)
}

open class VanillaRegistryWrapper<T : Any>(private val vanillaRegistry: Registry<T>) : RegistryWrapper<ResourceLocation, T>() {
    private val registrars = mutableListOf<ContentRegistrar<ResourceLocation, T>>()

    override fun get(key: ResourceLocation?) = vanillaRegistry[key]
    override fun getKey(value: T?) = vanillaRegistry.getKey(value)

    override fun initialize(owner: ContentRegistrar<ResourceLocation, T>) {
        registrars.add(owner)
    }
}
