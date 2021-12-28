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
import java.util.*

class FishingRod(var rod: ItemStack) {
    var remainingFoodCount = 0
    var currentFood = ArrayList(listOf(0.0, 0.0, 0.0, 0.0, 0.0))
}