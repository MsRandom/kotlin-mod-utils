package net.msrandom.kotlinutils

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraftforge.fml.common.network.IGuiHandler
import net.msrandom.kotlinutils.registry.CONTAINER_REGISTRY

open class KotlinUtilsSidedProxy : IGuiHandler {
    override fun getServerGuiElement(id: Int, player: Player, level: Level, x: Int, y: Int, z: Int) = container(id)?.create(player, makePos(x, y, z))
    override fun getClientGuiElement(id: Int, player: Player, level: Level, x: Int, y: Int, z: Int): Any? = null
    protected fun container(id: Int) = CONTAINER_REGISTRY[id]
    protected fun makePos(x: Int, y: Int, z: Int): BlockPos = if (x == 0 && y == 0 && z == 0) BlockPos.ZERO else BlockPos(x, y, z)
}
