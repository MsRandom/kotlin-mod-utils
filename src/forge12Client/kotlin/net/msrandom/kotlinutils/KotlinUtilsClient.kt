package net.msrandom.kotlinutils

import com.sun.org.apache.xpath.internal.operations.Mod
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(value = [Side.CLIENT], modid = KotlinUtils.MOD_ID)
object KotlinUtilsClient {
    private val guiFactorySuppliers = hashMapOf<() -> MenuType<*>?, (AbstractContainerMenu) -> AbstractContainerScreen>()
    private val guiFactories = hashMapOf<MenuType<*>, (AbstractContainerMenu) -> AbstractContainerScreen>()

    @SubscribeEvent
    @JvmStatic
    fun loadModels(@Suppress("UNUSED_PARAMETER") event: ModelRegistryEvent) {
        for ((typeSupplier, factory) in guiFactorySuppliers) {
            typeSupplier()?.let { guiFactories[it] = factory }
        }

        guiFactorySuppliers.clear()
    }

    @JvmStatic
    fun getGuiFactory(type: MenuType<*>) = guiFactories[type]

    @JvmStatic
    fun <T : AbstractContainerMenu> registerGuiFactory(
        type: () -> MenuType<*>?,
        factory: () -> (container: T) -> AbstractContainerScreen
    ) {
        @Suppress("UNCHECKED_CAST")
        guiFactorySuppliers[type] = factory() as (AbstractContainerMenu) -> AbstractContainerScreen
    }
}
