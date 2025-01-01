package net.msrandom.kotlinutils.registry

import net.minecraft.resources.ResourceLocation

actual fun ResourceLocation(namespace: String, path: String) = ResourceLocation(namespace, path)
