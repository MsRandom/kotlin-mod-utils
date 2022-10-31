package net.msrandom.kotlinutils

import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.msrandom.kotlinutils.KotlinUtils.Companion.deferredRegistrars

@Suppress("UNUSED_PARAMETER")
@Mod(modid = KotlinUtils.MOD_ID, useMetadata = true)
class KotlinUtilsForge {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        var sum = 0
        for (registrar in deferredRegistrars) {
            for ((supplier) in registrar.values) {
                supplier.update()
                ++sum
            }
        }

        deferredRegistrars.clear()
    }

    companion object {
        @field:SidedProxy(serverSide = "net.msrandom.kotlinutils.KotlinUtilsSidedProxy", clientSide = "net.msrandom.kotlinutils.KotlinUtilsClientSidedProxy")
        lateinit var proxy: KotlinUtilsSidedProxy
    }
}
