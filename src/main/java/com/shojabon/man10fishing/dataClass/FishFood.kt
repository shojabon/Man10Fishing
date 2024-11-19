package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.mcutils.Utils.BaseUtils
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SItemStack
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.*

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

            for(i in 0..food1Type.size-2){
                result.add(min(max(1.1*(food1Type[i] + food2Type[i])/2,-500.0),500.0))
            }


            val distance= nDimensionDistanceSquared(food1Type.subList(0,5),food2Type.subList(0,5))
            val food1Amount=food1.food.amount.toDouble()
            val food2Amount=food2.food.amount.toDouble()
            val amountDif= max(food1Amount/food2Amount,food2Amount/food1Amount)-1
            val multiply=((log(10.0+(distance/50000.0)*amountDif,10.0)*7.0*distance/1400000.0)/3.0)+(3.0/4.0)
            val distanceMultiply=min(max(((food1Type[5]+food2Type[5])/900)*log(10+distance/50000,10.0),0.4),1.0)
            val rawFoodRange=floor(distanceMultiply*multiply*(food1Type[5]+food2Type[5])/2)
            val foodRange=(if(rawFoodRange>800)800.0 else if(rawFoodRange<0) 0.0 else rawFoodRange)

            result.add(foodRange)

            return result
        }

        fun getFoodTypeNameFromStr(data:List<String>):String{
            val numData= mutableListOf<Double>()
            data.forEach { numData.add(it.toDoubleOrNull()?:return "§4不正なデータ") }
            return getFoodTypeName(numData)
        }


        fun getFoodTypeName(data:List<Double>):String{
            if(data.size<6)return "§4不正なデータ"
            var name="§a"
            if(data[5]<50)name+="洗練された"

            val tasteData=data.subList(0,5)

            val sum=tasteData.sum()

            if(sum<-1750)name+="味の薄い"
            else if(sum>1750)name+="味の濃ゆい"
            else{
                val max=tasteData.maxOrNull()?:0.0

                if(max*5-sum>800){
                    when(tasteData.indexOf(max)){

                        0->name+="甘い"
                        1->name+="酸っぱい"
                        2->name+="旨い"
                        3->name+="苦い"
                        4->name+="匂いの強い"

                    }
                }
                else{
                    name+="バランスの良い"
                }
            }

            name+="餌"

            return name
        }

        fun getFoodTypeLoreFromStr(data:List<String>):List<String>{
            val numData= mutableListOf<Double>()
            data.forEach { numData.add(it.toDoubleOrNull()?:return listOf("§4不正なデータ")) }
            return getFoodTypeLore(numData)
        }

        fun getColor(data:List<Double>):Color{

            val tasteData=data.subList(0,5)

            val sum=tasteData.sum()

            if(sum<-1750)return Color.GRAY
            if(sum>1750)return Color.BLACK
            val max=tasteData.maxOrNull()?:0.0
            return when(tasteData.indexOf(max)) {
                0 -> Color.MAROON
                1 -> Color.YELLOW
                2 -> Color.ORANGE
                3 -> Color.GREEN
                4 -> Color.SILVER
                else -> Color.WHITE
            }
        }

        fun getFoodTypeLore(data: List<Double>):List<String>{

            if(data.size<6)return listOf("§4不正なデータ")
            val lore= mutableListOf("§e甘味§3：§f§l","§e酸味§3：§f§l","§e旨味§3：§f§l","§e苦味§3：§f§l","§e匂い§3：§f§l","","§7味のぶれ：§c")

            for(i in 0 until 5){
                var count=-400
                for(j in 0 until 5){
                    lore[i]+=if(data[i]<count) "§c■" else "§0□"
                    count+=100
                }
                lore[i]+="§f|"
                count=0
                for(j in 0 until 5){
                    lore[i]+=if(data[i]>count) "§b■" else "§0□"
                    count+=100
                }
            }

            when{
                (data[5]<150)->{lore[6]=lore[6]+"極めて小さい"}
                (150<=data[5]&&data[5]<300)->{lore[6]=lore[6]+"小さい"}
                (300<=data[5]&&data[5]<500)->{lore[6]=lore[6]+"普通"}
                (500<=data[5]&&data[5]<650)->{lore[6]=lore[6]+"大きい"}
                (650<=data[5])->{lore[6]=lore[6]+"極めて大きい"}
            }

            return lore
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

    fun isUnMixable():Boolean{
        if(!food.itemMeta.persistentDataContainer.has(NamespacedKey(Man10Fishing.instance,"unmixable"), PersistentDataType.INTEGER))return false

        return food.itemMeta.persistentDataContainer.get(NamespacedKey(Man10Fishing.instance,"unmixable"), PersistentDataType.INTEGER)==1
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