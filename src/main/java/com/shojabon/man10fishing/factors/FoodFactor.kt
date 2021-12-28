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

@FishFactorDefinition(name = "フード",
        iconMaterial = Material.MELON_SEEDS,
        explanation = ["食べ物ロジックの定義"],
        settable = false)
class FoodFactor(fish: Fish) : FishFactor(fish) {

    var matrix = FishSettingVariable("food.matrix", listOf(0.0, 0.0, 0.0, 0.0, 0.0))
    var range = FishSettingVariable("food.range", 0.0)

    override fun rarityMultiplier(fish: Fish, currentMultiplier: Float, fisher: Player, rod: FishingRod): Float {
        return if (nDimensionDistanceSquared(matrix.get()!!, rod.currentFood) <= (range.get()!!).pow(2.0)) {
            currentMultiplier * foodInRangeMultiplier
        } else 1f
    }

    private fun nDimensionDistanceSquared(origin: List<Double>, target: List<Double?>?): Double {
        var result = 0.0
        if (origin.size != target!!.size) return (-1).toDouble()
        for (i in origin.indices) {
            result += (target[i]!! - origin[i]).pow(2.0)
        }
        return result
    }

}