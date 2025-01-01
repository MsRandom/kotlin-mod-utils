package net.msrandom.kotlinutils.config

import net.msrandom.stub.Stub
import java.nio.file.Path

@Stub
expect val CONFIG_PATH: Path

@Stub
expect enum class ConfigSide {
    CLIENT,
    SERVER
}
