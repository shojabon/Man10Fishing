package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.mcutils.Utils.BaseUtils
import com.shojabon.mcutils.Utils.SInventory.SInventory
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
            return SItemStack(item).getCustomData(Man10Fishing.instance, "foodCount") == null
        }

        //デフォルト設定があるアイテムなら
        fun isDefaultFood(item: ItemStack): Boolean{
            val defaultFoodType = Man10Fishing.foodConfig.getString(item.type.name) ?: return false
            val foodSeparatedInformation = defaultFoodType.split("|")
            if(foodSeparatedInformation.size != 6){
                return false
            }
            for(foodInfo in foodSeparatedInformation){
                if(!BaseUtils.isInt(foodInfo)){
                    return false
                }
            }
            return true
        }

        //餌合成
        fun mixFoodType(food1: FishFood, food2: FishFood): List<Double>?{
            val result = ArrayList<Double>()

            val food1Type = food1.getFoodTypeList() ?: return null
            val food2Type = food2.getFoodTypeList() ?: return null

            for(i in food1Type.indices){
                result.add((food1Type[i] + food2Type[i])/2)
            }
            return result
        }

        fun getFoodTypeLoreFromStr(data:List<String>):List<String>{
            val numData= mutableListOf<Double>()
            data.forEach { numData.add(it.toDoubleOrNull()?:return listOf("§4不正なデータ")) }
            return getFoodTypeLore(numData)
        }

        fun getFoodTypeLore(data: List<Double>):List<String>{

            if(data.size<6)return listOf("§4不正なデータ")
            val lore= mutableListOf("§e甘味§3：§f§l■","§e酸味§3：§f§l■","§e旨味§3：§f§l■","§e苦味§3：§f§l■","§e匂い§3：§f§l■","","§7味のぶれ：§c")

            for(i in 0 until 5){
                var count=-400
                for(j in 0 until 9){
                    if(data[i]<count)break
                    lore[i]+="■"
                    count+=100
                }
            }

            when{
                (data[5]<100)->{lore[6]=lore[6]+"極めて小さい"}
                (100<=data[5]&&data[5]<200)->{lore[6]=lore[6]+"小さい"}
                (200<=data[5]&&data[5]<300)->{lore[6]=lore[6]+"普通"}
                (300<=data[5]&&data[5]<400)->{lore[6]=lore[6]+"大きい"}
                (400<=data[5]&&data[5]<500)->{lore[6]=lore[6]+"極めて大きい"}
            }

            return lore
        }
    }

    fun getFoodTypeString(): String?{
        if(!isFood(food)) return null
        if(isEmbeddedFood(food)) return SItemStack(food).getCustomData(Man10Fishing.instance, "foodType")
        val defaultFoodType = Man10Fishing.foodConfig.getString(food.type.name) ?: return null

        val foodSeparatedInformation = defaultFoodType.split("|")
        if(foodSeparatedInformation.size != 6){
            return null
        }
        for(foodInfo in foodSeparatedInformation){
            if(!BaseUtils.isInt(foodInfo)){
                return null
            }
        }
        return defaultFoodType
    }

    fun getFoodTypeList(): List<Double>?{
        val result = ArrayList<Double>()
        val foodTypeString = this.getFoodTypeString() ?: return null

        val foodSeparatedInformation = foodTypeString.split("|")
        if(foodSeparatedInformation.size != 6){
            return null
        }
        for(foodInfo in foodSeparatedInformation){
            if(!BaseUtils.isDouble(foodInfo)){
                return null
            }
            result.add(foodInfo.toDouble())
        }
        return result
    }

    fun setFoodTypeString(string: String){
        val result = SItemStack(food).setCustomData(Man10Fishing.instance, "foodType", string)
        food = result.build()
    }

    fun setFoodTypeList(data: List<Double>){

        var resultData = ""
        for(i in data.indices){
            resultData += data[i].toString() + "|"
        }
        resultData = resultData.substring(0, resultData.length-1)

        val result = SItemStack(food).setCustomData(Man10Fishing.instance, "foodType", resultData)
        food = result.build()
    }
}