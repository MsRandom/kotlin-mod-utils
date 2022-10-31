package net.msrandom.kotlinutils

import com.sun.org.apache.xpath.internal.operations.Mod
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.msrandom.kotlinutils.registry.ContentRegistrar

@Suppress("UNUSED_PARAMETER")
@Mod(modid = KotlinUtils.MOD_ID, useMetadata = true)
class KotlinUtils {
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
        const val MOD_ID = "kotlinutils"
        internal val deferredRegistrars = hashSetOf<ContentRegistrar<*, *>>()

        @field:SidedProxy(serverSide = "net.msrandom.kotlinutils.KotlinUtilsSidedProxy", clientSide = "net.msrandom.kotlinutils.KotlinUtilsClientSidedProxy")
        lateinit var proxy: KotlinUtilsSidedProxy
    }
}
