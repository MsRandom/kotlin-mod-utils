package net.msrandom.kotlinutils

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

class MenuType<T : AbstractContainerMenu>(private val factory: (Player, BlockPos) -> T) {
    fun create(player: Player, pos: BlockPos) = factory(player, pos)
}
