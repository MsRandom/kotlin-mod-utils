@file:Mixin(RegistryAccess::class)
@file:JvmName("RegistryAccessMixin")
package net.msrandom.kotlinutils.mixin

import com.google.common.collect.ImmutableMap
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.msrandom.kotlinutils.registry.codecRegistries
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.spongepowered.asm.mixin.injection.callback.LocalCapture

@Inject(at = [At("TAIl")], method = ["lambda\$static\$1"], locals = LocalCapture.CAPTURE_FAILHARD)
private fun putRegistries(
    callbackInfoReturnable: CallbackInfoReturnable<ImmutableMap<ResourceKey<out Registry<*>>, RegistryAccess.RegistryData<*>>>,
    builder: ImmutableMap.Builder<ResourceKey<out Registry<*>>, RegistryAccess.RegistryData<*>>
) {
    builder.putAll(codecRegistries)
}
