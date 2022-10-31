package net.msrandom.kotlinutils.registry;

import kotlin.jvm.functions.Function0;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Not in kotlin due to rawtypes and unchecked warnings being better fit for internal Java code
@SuppressWarnings({"rawtypes", "unchecked"})
public class ForgePlatformRegistryHandler<T extends IForgeRegistryEntry<T>> {
    private final Map<RegistrySupplier<ResourceLocation, ? extends T>, Function0<T>> values;
    private final IForgeRegistry<T> registry;

    public ForgePlatformRegistryHandler(@NotNull Map<RegistrySupplier<ResourceLocation, ? extends T>, Function0<T>> values, IForgeRegistry<T> registry) {
        this.values = values;
        this.registry = registry;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void register(RegistryEvent.Register event) {
        if (event.getGenericType() == registry.getRegistrySuperType()) {
            handle(event);
        }
    }

    protected void handle(RegistryEvent.Register<T> event) {
        for (Map.Entry<RegistrySupplier<ResourceLocation, ? extends T>, Function0<T>> entry : values.entrySet()) {
            T value = entry.getValue().invoke();
            event.getRegistry().register(createName(entry.getKey().getRegistryKey(), value));
            entry.getKey().update();
        }
    }

    protected T createName(ResourceLocation name, T value) {
        return value.setRegistryName(name);
    }

    public static <T extends BlockEntity> Function0<T> getTileFactory(Class<T> type) throws Throwable {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final MethodHandle handle = lookup.findConstructor(type, MethodType.methodType(Void.TYPE));
        return (Function0<T>) LambdaMetafactory.metafactory(
                lookup,
                "invoke",
                MethodType.methodType(Function0.class),
                MethodType.methodType(Void.TYPE),
                handle,
                handle.type()
        ).getTarget().invokeExact();
    }

    static class RegistryFinalizer<T extends IForgeRegistryEntry<T>> {
        private final IForgeRegistry<T> registry;
        private final Set<ContentRegistrar<ResourceLocation, ?>> deferredRegistrars = new HashSet<>();

        RegistryFinalizer(IForgeRegistry<T> registry) {
            this.registry = registry;
        }

        //AFTER (hopefully)everything else in this registry is registered, we update our references.
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void register(RegistryEvent.Register event) {
            if (event.getGenericType() == registry.getRegistrySuperType()) {
                for (ContentRegistrar<ResourceLocation, ?> deferredRegistrar : deferredRegistrars) {
                    for (RegistrySupplier<ResourceLocation, ?> supplier : deferredRegistrar.getValues().keySet()) {
                        supplier.update();
                    }
                }
            }
        }

        void add(ContentRegistrar<ResourceLocation, ?> registrar) {
            deferredRegistrars.add(registrar);
        }
    }
}
