package net.msrandom.kotlinutils.registry

import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface RegistrySupplier<T : Any> : ReadOnlyProperty<Any?, T>, Supplier<T>, () -> T {
    val registryKey: ResourceLocation

    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    override operator fun invoke() = get()
}
