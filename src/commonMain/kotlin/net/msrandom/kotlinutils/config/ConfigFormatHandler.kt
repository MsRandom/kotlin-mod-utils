package net.msrandom.kotlinutils.config

import com.mojang.serialization.DynamicOps
import java.io.InputStream
import java.io.OutputStream

class ConfigFormatHandler<T : Any>(
    val extension: String,
    val ops: DynamicOps<T>,
    val nameConvertor: (String) -> String,
    val serialize: OutputStream.(T) -> Unit,
    val deserialize: InputStream.() -> Unit
)
