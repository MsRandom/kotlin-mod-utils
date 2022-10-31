package net.msrandom.kotlinutils

import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class KotlinUtilsClientSidedProxy : KotlinUtilsSidedProxy() {
    override fun getClientGuiElement(id: Int, player: Player, level: Level, x: Int, y: Int, z: Int) = container(id)?.let {
        KotlinUtilsClient.getGuiFactory(it)?.invoke(it.create(player, makePos(x, y, z)))
    }
}
