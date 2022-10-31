package net.msrandom.kotlinutils.config

import java.nio.file.Path

expect val CONFIG_PATH: Path

expect enum class ConfigSide {
    CLIENT,
    SERVER
}
