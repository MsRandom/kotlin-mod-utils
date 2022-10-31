package net.msrandom.kotlinutils

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.msrandom.kotlinutils.registry.ForgePlatformRegistryHandler

interface BlockEntityType<T : BlockEntity> {
    val entityClass: Class<T>

    fun create(): T?

    @Suppress("UNCHECKED_CAST")
    fun getAt(blockReader: BlockGetter, pos: BlockPos) = blockReader.getBlockEntity(pos) as? T

    class Builder<T : BlockEntity> private constructor(private val factory: () -> T?, private val blocks: Set<Block>) {
        private lateinit var entityClass: Class<T>

        fun entity(entityClass: Class<T>): Builder<T> {
            this.entityClass = entityClass
            return this
        }

        fun build(@Suppress("UNUSED_PARAMETER") datafixerType: Any?): BlockEntityType<T> = BuiltBlockEntityType(entityClass, factory, blocks)

        companion object {
            @JvmStatic
            fun <T : BlockEntity> create(factory: () -> T, vararg validBlocks: Block) =
                Builder(factory, setOf(*validBlocks))
        }
    }
}

internal class BuiltBlockEntityType<T : BlockEntity>(override val entityClass: Class<T>, private val factory: () -> T?, @Suppress("UNUSED_PARAMETER") validBlocks: Set<Block>) :
    BlockEntityType<T> {
    override fun create() = factory()
}

internal class WrappingBlockEntityType<T : BlockEntity>(override val entityClass: Class<T>) : BlockEntityType<T> {
    companion object {
        private val cache = hashMapOf<Class<out BlockEntity>, WrappingBlockEntityType<*>>()

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        operator fun <T : BlockEntity> get(entityClass: Class<T>) =
            cache.computeIfAbsent(entityClass) { WrappingBlockEntityType(it) } as BlockEntityType<T>
    }

    private val factory by lazy {
        try {
            ForgePlatformRegistryHandler.getTileFactory(entityClass)
        } catch (throwable: Throwable) {
            null
        }
    }

    override fun create() = factory?.invoke()
}
