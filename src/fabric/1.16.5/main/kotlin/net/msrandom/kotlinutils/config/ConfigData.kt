package net.msrandom.kotlinutils.config

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

actual val CONFIG_PATH: Path = FabricLoader.getInstance().configDir

actual typealias ConfigSide = EnvType
