package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import org.bukkit.Material
import com.shojabon.man10fishing.dataClass.FishFactor
import org.bukkit.entity.Player
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import org.bukkit.Bukkit
import kotlin.math.pow
import kotlin.random.Random

@FishFactorDefinition(name = "サイズ設定",
        iconMaterial = Material.CHEST,
        explanation = ["サイズ定義"],
        adminSetting = false,
        settable = false)
class SizeFactor(fish: Fish) : FishFactor(fish) {

    var min = FishSettingVariable("size.min", 0.0)
    var max = FishSettingVariable("size.max", 0.0)

    fun generateRandomSize(): Double {
        if(max.get() < min.get()) return -1.0
        return Random.nextDouble(min.get(), max.get())
    }


}