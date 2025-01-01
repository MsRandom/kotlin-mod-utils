package net.msrandom.kotlinutils.registry;

import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.reflect.KProperty;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class RegistryCreationImpl {
    private static final Constructor<Registry<?>> mappedRegistryWrapperFactory;

    static {
        try {
            mappedRegistryWrapperFactory = (Constructor<Registry<?>>) Class.forName("net.minecraftforge.registries.NamespacedWrapper").getConstructor(ForgeRegistry.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        mappedRegistryWrapperFactory.setAccessible(true);
    }

    public static <T> RegistryProvider<T> createForgeRegistry(ResourceLocation id, Class<T> type) {
        return (RegistryProvider<T>) createForgeRegistryImpl(id, type);
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryProvider<T> createForgeRegistryImpl(ResourceLocation id, Class<?> type) {
        IForgeRegistry<T> forgeRegistry = new RegistryBuilder<T>().setName(id).setType((Class<T>) type).create();

        Lazy<Registry<T>> lazy = LazyKt.lazy(LazyThreadSafetyMode.NONE, () -> {
            try {
                return (MappedRegistry<T>) mappedRegistryWrapperFactory.newInstance(forgeRegistry);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        return new RegistryProvider<T>() {
            @Override
            public IForgeRegistry<?> getForgeRegistry() {
                return forgeRegistry;
            }

            @Override
            public @NotNull Registry<T> invoke() {
                return lazy.getValue();
            }

            @Override
            public @NotNull Registry<T> get() {
                return lazy.getValue();
            }

            @Override
            public @NotNull Registry<T> getValue(@Nullable Object o, @NotNull KProperty<?> kProperty) {
                return lazy.getValue();
            }
        };
    }

    static <T> RegistryProvider<T> getActiveRegistry(Registry<T> registry) {
        @Nullable ForgeRegistry<?> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registry.key().location());

        if (forgeRegistry == null) {
            return RegistriesKt.RegistryProvider(registry);
        }

        return new RegistryProvider<T>() {
            @Override
            public IForgeRegistry<?> getForgeRegistry() {
                return forgeRegistry;
            }

            @Override
            @NotNull
            public Registry<T> invoke() {
                return registry;
            }

            @Override
            @NotNull
            public Registry<T> get() {
                return registry;
            }

            @Override
            @NotNull
            public Registry<T> getValue(@Nullable Object o, @NotNull KProperty<?> kProperty) {
                return registry;
            }
        };
    }

    static RegistryObject<?> registerDeferred(DeferredRegister<?> register, String name, Supplier<?> valueSupplier) {
        return register.register(name, uncheckedCast(valueSupplier));
    }

    private static <T, U> U uncheckedCast(T value) {
        return (U) value;
    }
}
