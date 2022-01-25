package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class FishingRod(var rodItem: ItemStack) {
    companion object{
        fun isRod(item: ItemStack): Boolean{
            if(item.type != Material.FISHING_ROD) return false
            val sItem = SItemStack(item)
            if(sItem.getCustomData(Man10Fishing.instance, "foodCount") == null) return false
            if(sItem.getCustomData(Man10Fishing.instance, "foodType") == null) return false
            return true
        }
    }

    var remainingFoodCount = 0
    var currentFood = listOf(0.0, 0.0, 0.0, 0.0, 0.0)

    init {
        if (isRod(rodItem)){
            val sItem = SItemStack(rodItem)
            remainingFoodCount = sItem.getCustomData(Man10Fishing.instance, "foodCount").toInt()

            currentFood = sItem.getCustomData(Man10Fishing.instance, "foodType").split("|").map { it.toDouble() }
        }
    }

    //食べ物回数

    fun getFoodCount(): Int {
        return remainingFoodCount
    }

    fun setFoodCount(count: Int){
        remainingFoodCount = count
        if(remainingFoodCount < 0) remainingFoodCount = 0
        rodItem = SItemStack(rodItem).setCustomData(Man10Fishing.instance, "foodCount", remainingFoodCount.toString()).build()
    }

    fun addFoodCount(count: Int){
        setFoodCount(getFoodCount() + count)
    }

    fun removeFoodCount(count: Int){
        setFoodCount(getFoodCount() - count)
    }

    //食べ物タイプ

    fun setFoodType(foodType: List<Double>){
        currentFood = foodType
        var foodString = ""
        for(foodElement in currentFood){
            foodString += "$foodElement|"
        }
        foodString = foodString.dropLast(1)
        rodItem = SItemStack(rodItem).setCustomData(Man10Fishing.instance, "foodType", foodString).build()
    }

    fun getFoodType(): List<Double>{
        return currentFood
    }
}