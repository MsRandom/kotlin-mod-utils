package net.msrandom.kotlinutils.config

import com.google.common.base.CaseFormat
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.msrandom.kotlinutils.DelegateProvider
import net.msrandom.kotlinutils.registry.ExternalRegistry
import java.lang.reflect.Type
import java.nio.file.Files
import java.util.*
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class ConfigModule(name: String, format: ConfigFormatHandler<*>) {
    private val format = format as ConfigFormatHandler<Any>
    private val file = CONFIG_PATH.resolve("$name.${format.extension}")
    private var root = this.format.ops.createMap(mapOf())

    private val _entries = WeakHashMap<String, CodecEntry<*>>()

    val entries: Map<String, CodecEntry<*>>
        get() = _entries

    private var pending = this.format.ops.mapBuilder()

    init {
        root = if (Files.exists(file)) {
            Files.newInputStream(file).use(this.format.deserialize)
        } else {
            this.format.ops.createMap(mapOf())
        }
    }

    fun <T> makeEntry(name: String, type: Type, defaultValue: T, changeLevel: ChangeLevel, side: ConfigSide) =
        CodecEntry(this, defaultValue, null, type, changeLevel, side, name)

    fun <T> makeEntry(name: String, type: Type, defaultValue: T, sanitizer: (T) -> T, changeLevel: ChangeLevel, side: ConfigSide) =
        CodecEntry(this, defaultValue, sanitizer, type, changeLevel, side, name)

    @JvmOverloads
    fun <T> get(name: String, defaultValue: T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        makeEntry(name, getType(defaultValue), defaultValue, changeLevel, side)

    @JvmOverloads
    fun <T> get(name: String, defaultValue: T, sanitizer: (T) -> T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        makeEntry(name, getType(defaultValue), defaultValue, sanitizer, changeLevel, side)

    @JvmOverloads
    fun <T> get(name: String, type: Type, defaultValue: T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        makeEntry(name, type, defaultValue, changeLevel, side)

    @JvmOverloads
    fun <T> get(name: String, type: Type, defaultValue: T, sanitizer: (T) -> T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        makeEntry(name, type, defaultValue, sanitizer, changeLevel, side)

    @JvmSynthetic
    inline operator fun <reified T> invoke(defaultValue: T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        DelegateProvider { makeEntry(it, object : TypeToken<T>() {}.type, defaultValue, changeLevel, side) }

    @JvmSynthetic
    inline operator fun <reified T : Comparable<T>> invoke(defaultValue: T, noinline sanitizer: (T) -> T, changeLevel: ChangeLevel = ChangeLevel.IMMEDIATE, side: ConfigSide = ConfigSide.SERVER) =
        DelegateProvider { makeEntry(it, object : TypeToken<T>() {}.type, defaultValue, sanitizer, changeLevel, side) }

    private fun getType(value: Any?): Type = when (value) {
        is Boolean -> Boolean::class.java
        is Byte -> Byte::class.java
        is Short -> Short::class.java
        is Int -> Int::class.java
        is Char -> Char::class.java
        is Long -> Long::class.java
        is Float -> Float::class.java
        is Double -> Double::class.java
        null -> Nothing::class.java
        else -> value.javaClass
    }

    fun update() {
        root = pending.build(root).result().orElse(root)
        pending = format.ops.mapBuilder()
        Files.newOutputStream(file).use { format.serialize(it, root) }
    }

    companion object {
        @JvmField
        val CODEC_REGISTRY = ExternalRegistry<Type, Codec<*>>()

        @JvmField
        val JSON = run {
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
            ConfigFormatHandler<JsonElement>(
                "json",
                JsonOps.INSTANCE,
                { CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, it) },
                { write(gson.toJson(it).toByteArray()) },
                { JsonParser.parseReader(reader()) }
            )
        }

        fun <T> getCodec(type: Type) =
            CODEC_REGISTRY[type] as? Codec<T> ?: throw IllegalStateException("Couldn't serialize type $type to a config entry as no codec was registered.")
    }

    class CodecEntry<T>(
        private val config: ConfigModule,
        val defaultValue: T,
        val sanitizer: ((T) -> T)?,
        val type: Type,
        val changeLevel: ChangeLevel,
        val side: ConfigSide,
        name: String
    ) : ReadOnlyProperty<Any?, T>, Supplier<T>, () -> T {
        private val name = config.format.nameConvertor(name)
        private var value: T? = null
        private var initialized = false

        init {
            config._entries[this.name] = this
        }

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>) = get()

        override fun get(): T {
            if (!initialized) {
                val configValue = config.format.ops.get(config.root, name).result()
                if (configValue.isPresent) {
                    val existingValue = getCodec<T>(type).decode(config.format.ops, configValue.get()).result().map(Pair<T, *>::getFirst).orElse(defaultValue)
                    if (sanitizer == null) {
                        value = existingValue
                    } else {
                        val newValue = sanitizer.invoke(existingValue)
                        if (existingValue == newValue) {
                            value = existingValue
                        } else {
                            set(newValue)
                        }
                    }
                } else {
                    set(defaultValue)
                }
                initialized = true
            }
            return value as T
        }

        override operator fun invoke() = get()

        fun set(value: T) {
            config.pending.add(name, value, getCodec<T>(type))
            initialized = true
            this.value = value
        }
    }

    internal enum class ChangeHandling {
        NONE,
        DATA,
        WORLD,
        GAME
    }

    enum class ChangeLevel(internal val changeHandling: ChangeHandling) {
        IMMEDIATE(ChangeHandling.NONE),
        RELOADABLE_DATA(ChangeHandling.DATA),
        RECIPE_DATA(ChangeHandling.GAME),
        WORLD_DATA(ChangeHandling.WORLD),
        RESTART_GAME(ChangeHandling.GAME)
    }
}
