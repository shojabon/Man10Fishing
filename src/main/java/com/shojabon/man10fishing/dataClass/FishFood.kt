package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.mcutils.Utils.BaseUtils
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

class FishFood(var food: ItemStack) {
    companion object {
        fun isFood(item: ItemStack): Boolean{
            return isEmbeddedFood(item) || isDefaultFood(item)
        }
        //データが埋め込まれてるなら
        fun isEmbeddedFood(item: ItemStack): Boolean {
            if (SItemStack(item).getCustomData(Man10Fishing.instance, "foodType") == null) return false
            if (SItemStack(item).getCustomData(Man10Fishing.instance, "foodCount") != null) return false
            return true
        }

        //デフォルト設定があるアイテムなら
        fun isDefaultFood(item: ItemStack): Boolean{
            val defaultFoodType = Man10Fishing.foodConfig.getString(item.type.name) ?: return false
            val foodSeparatedInformation = defaultFoodType.split("|")
            if(foodSeparatedInformation.size != 5){
                return false
            }
            for(foodInfo in foodSeparatedInformation){
                if(!BaseUtils.isInt(foodInfo)){
                    return false
                }
            }
            return true
        }
    }

    fun getFoodType(): String?{
        if(!isFood(food)) return null
        if(isEmbeddedFood(food)) return SItemStack(food).getCustomData(Man10Fishing.instance, "foodType")
        val defaultFoodType = Man10Fishing.foodConfig.getString(food.type.name) ?: return null

        val foodSeparatedInformation = defaultFoodType.split("|")
        if(foodSeparatedInformation.size != 5){
            return null
        }
        for(foodInfo in foodSeparatedInformation){
            if(!BaseUtils.isInt(foodInfo)){
                return null
            }
        }
        return defaultFoodType
    }
}