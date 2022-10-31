package net.msrandom.kotlinutils

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.msrandom.kotlinutils.KotlinUtils.Companion.deferredRegistrars

@Suppress("UNUSED_PARAMETER")
@Mod(KotlinUtils.MOD_ID)
class KotlinUtilsForge {
    init {
        FMLJavaModLoadingContext.get().modEventBus.addListener(::init)
    }

    private fun init(event: FMLCommonSetupEvent) {
        for (registrar in deferredRegistrars) {
            for ((supplier) in registrar.values) {
                supplier.update()
            }
        }

        deferredRegistrars.clear()
    }
}
