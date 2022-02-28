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
import kotlin.math.round
import kotlin.random.Random

@FishFactorDefinition(name = "ウェイト設定",
        iconMaterial = Material.CLOCK,
        explanation = ["重量の定義"],
        adminSetting = false,
        settable = false)
class WeightFactor(fish: Fish) : FishFactor(fish) {

    var min = FishSettingVariable("weight.min", 0.0)
    var max = FishSettingVariable("weight.max", 1.0)

    fun generateRandomWeight(): Double {
        if(max.get() < min.get()) return -1.0
        return round(Random.nextDouble(min.get(), max.get()) * 10) / 10
    }


}