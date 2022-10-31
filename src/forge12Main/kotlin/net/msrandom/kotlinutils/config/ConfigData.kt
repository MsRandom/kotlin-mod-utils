package net.msrandom.kotlinutils.config

import jdk.internal.loader.Loader
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.relauncher.Side
import java.nio.file.Path

actual val CONFIG_PATH: Path = Loader.instance().configDir.toPath()

actual typealias ConfigSide = Side
