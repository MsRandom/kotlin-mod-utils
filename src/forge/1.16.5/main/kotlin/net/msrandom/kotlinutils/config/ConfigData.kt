package net.msrandom.kotlinutils.config

import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.loading.FMLConfig
import net.minecraftforge.fml.loading.FMLPaths
import java.nio.file.Path

actual val CONFIG_PATH: Path = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath())

actual typealias ConfigSide = LogicalSide
