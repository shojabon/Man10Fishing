package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.dataClass.Fish.Companion.settingTypeMap
import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import org.bukkit.inventory.ItemStack
import org.bukkit.configuration.ConfigurationSection
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import java.lang.reflect.ParameterizedType
import java.util.stream.Collectors
import com.shojabon.mcutils.Utils.SItemStack
import com.shojabon.mcutils.Utils.SConfigFile
import org.bukkit.configuration.file.YamlConfiguration
import com.shojabon.man10fishing.annotations.FoodFactorDefinition
import org.bukkit.Material
import com.shojabon.man10fishing.dataClass.FishFactor
import org.bukkit.entity.Player
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.Man10Fishing
import java.lang.Exception
import java.lang.reflect.Type
import java.util.*
import java.util.function.Function
import java.util.stream.Stream

class FishSettingVariable<T>(var settingId: String, val defaultValue: T) {
    var config: ConfigurationSection? = null
    var value: T? = null
    val type: Type?
        get() = settingTypeMap[settingId]

    // noinspection unchecked
    val valueClass: Class<T>
        get() =// noinspection unchecked
            resolveBaseClass(type) as Class<T>

    fun set(value: T): Boolean {
        this.value = value
        config!!["fishFactors.$settingId"] = stringGet()
        return true
    }

    fun get(): T? {
        if (value != null) {
            return value
        }
        if (!config!!.contains("fishFactors.$settingId")) return defaultValue
        stringSet(config!!.getString("fishFactors.$settingId"))
        return value
    }

    fun stringSet(value: String?) {
        val parser = Parser.getParser(type)
        this.value = parser!!.parse(this, value) as T
    }

    fun stringGet(): String {
        val parser = Parser.getParser(type)
        return parser!!.toString(this, value)
    }

    enum class Parser {
        DOUBLE(Double::class.java, Function<String?, Double> { s: String? -> s!!.toDouble() }), BOOLEAN(Boolean::class.java, Function { s: String? -> java.lang.Boolean.parseBoolean(s) }), INTEGER(Int::class.java, Function<String?, Int> { s: String? -> s!!.toInt() }), FLOAT(Float::class.java, Function<String?, Float> { s: String? -> s!!.toFloat() }), LONG(Long::class.java, Function<String?, Long> { s: String? -> s!!.toLong() }), STRING(String::class.java, Function<String?, String> { original: String? -> String(original) }), UUID(UUID::class.java, Function<String?, UUID> { name: String? -> java.util.UUID.fromString(name) }), LIST {
            override fun parse(context: FishSettingVariable<*>, raw: String?): Any {
                val type = (context.type as ParameterizedType?)!!.actualTypeArguments[0]
                val parser = getParser(type)
                return Stream.of(*raw!!.split(",").toTypedArray())
                        .map { s: String? -> parser!!.parse(context, s) }
                        .collect(Collectors.toList())
            }

            override fun toString(context: FishSettingVariable<*>, value: Any): String {
                val type = (context.type as ParameterizedType?)!!.actualTypeArguments[0]
                val parser = getParser(type)
                return (value as List<*>).stream()
                        .map<String>({ o: Any -> parser!!.toString(context, o) })
                        .collect(Collectors.joining(","))
            }

            override fun accepts(type: Type?): Boolean {
                return MutableList::class.java.isAssignableFrom(resolveBaseClass(type))
            }
        },
        ITEM_STACK {
            override fun parse(context: FishSettingVariable<*>?, raw: String?): Any {
                return SItemStack.fromBase64(raw).build()
            }

            override fun toString(context: FishSettingVariable<*>?, value: Any): String {
                return SItemStack(value as ItemStack).base64
            }

            override fun accepts(type: Type?): Boolean {
                return ItemStack::class.java.isAssignableFrom(resolveBaseClass(type))
            }
        },
        YAML_CONFIG {
            override fun parse(context: FishSettingVariable<*>?, raw: String?): Any {
                return SConfigFile.loadConfigFromBase64(raw)
            }

            override fun toString(context: FishSettingVariable<*>?, value: Any): String {
                return SConfigFile.base64EncodeConfig(value as YamlConfiguration)
            }

            override fun accepts(type: Type?): Boolean {
                return YamlConfiguration::class.java.isAssignableFrom(resolveBaseClass(type))
            }
        },
        MAPPING {
            override fun parse(context: FishSettingVariable<*>, raw: String?): Any {
                val keyType = (context.type as ParameterizedType?)!!.actualTypeArguments[0]
                val valueType = (context.type as ParameterizedType?)!!.actualTypeArguments[1]
                val keyParser = getParser(keyType)
                val valueParser = getParser(valueType)
                return Stream.of(*raw!!.split(",(?=[^,]*->)").toTypedArray())
                        .map { s: String -> s.split("->").toTypedArray() }
                        .collect(Collectors.toMap<Array<String>, Any, Any>({ s: Array<String> -> keyParser!!.parse(context, s[0]) }) { s: Array<String> -> valueParser!!.parse(context, s[1]) })
            }

            override fun toString(context: FishSettingVariable<*>, value: Any): String {
                val keyType = (context.type as ParameterizedType?)!!.actualTypeArguments[0]
                val valueType = (context.type as ParameterizedType?)!!.actualTypeArguments[1]
                val keyParser = getParser(keyType)
                val valueParser = getParser(valueType)
                return (value as Map<*, *>).entries.stream()
                        .map(Function<Map.Entry<Any, Any>, String> { (key, value1): Map.Entry<Any, Any> -> keyParser!!.toString(context, key) + "->" + valueParser!!.toString(context, value1) })
                        .collect(Collectors.joining(","))
            }

            override fun accepts(type: Type?): Boolean {
                return MutableMap::class.java.isAssignableFrom(resolveBaseClass(type))
            }
        };

        private val `cla$$`: Class<*>?
        private val parser: Function<String?, Any>?
        private val toString: Function<Any, String>?

        constructor() {
            `cla$$` = null
            parser = null
            toString = null
        }

        constructor(`cla$$`: Class<T>, parser: Function<String?, T>) : this(`cla$$`, parser, Function<T, String> { obj: T -> obj.toString() }) {}
        constructor(`cla$$`: Class<T>, parser: Function<String?, T>, toString: Function<T, String>) {
            this.`cla$$` = `cla$$`
            this.parser = Function<String?, Any> { t: String? -> parser.apply(t) }
            this.toString = Function { x: Any? -> toString.apply(x as T?) }
        }

        open fun parse(setting: FishSettingVariable<*>?, raw: String?): Any {
            val parsed = parser!!.apply(raw)
            Objects.requireNonNull(parsed)
            return parsed
        }

        open fun toString(context: FishSettingVariable<*>?, value: Any): String {
            return toString!!.apply(value)
        }

        open fun accepts(type: Type?): Boolean {
            return type is Class<*> && `cla$$`!!.isAssignableFrom(type as Class<*>?)
        }

        companion object {
            fun getParser(type: Type?): Parser? {
                try {
                    return Stream.of(*values())
                            .filter { parser: Parser? -> parser!!.accepts(type) }
                            .findFirst().orElse(null)
                } catch (e: Exception) {
                    println(e.toString() + " " + type!!.typeName)
                }
                return null
            }
        }
    }

    companion object {
        fun resolveBaseClass(type: Type?): Class<*> {
            return if (type is Class<*>) type else if (type is ParameterizedType) (type.rawType as Class<*>) else null
        }
    }
}