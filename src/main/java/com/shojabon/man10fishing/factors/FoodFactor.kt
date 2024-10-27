package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import org.bukkit.Material
import org.bukkit.entity.Player
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Bukkit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

@FishFactorDefinition(name = "フード",
        iconMaterial = Material.MELON_SEEDS,
        explanation = ["食べ物ロジックの定義"],
        adminSetting = false,
        settable = false)
class FoodFactor(fish: Fish) : FishFactor(fish) {

    var matrix = FishSettingVariable("food.matrix", listOf(0.0, 0.0, 0.0, 0.0, 0.0))
    var range = FishSettingVariable("food.range", 0.0)

    override fun rarityMultiplier(fish: Fish, currentMultiplier: Float, fisher: Player, rod: FishingRod): Float {

        //food,fishの半径及び中心の距離をとり、円同士が重なっているかどうかを半径と中心の距離との関係で評価
        // x<y+z == x^2<(y+z)^2を利用
        val foodRange=if(rod.currentFood[5]>0)rod.currentFood[5] else 0.0
        val fishRange=range.get()!!
        val poweredDistance=nDimensionDistanceSquared(matrix.get()!!, rod.currentFood.subList(0,5))
        val rangeDistanceMultiplier=if(foodRange<0.0) 2.5F else max((750.0F/foodRange.toFloat()),1.0F)

        return if (poweredDistance <= foodRange.pow(2.0)+fishRange.pow(2.0)+2*foodRange*fishRange) {
            currentMultiplier * foodInRangeMultiplier*rangeDistanceMultiplier
        } else currentMultiplier
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