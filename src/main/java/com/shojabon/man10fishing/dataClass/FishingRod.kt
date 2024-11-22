package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.mcutils.Utils.BaseUtils
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

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
    var currentFood = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    var season= Season.NONE
    var foodPerUse=1
    var savingChance=0.0

    init {
        if (isRod(rodItem)){
            val sItem = SItemStack(rodItem)
            remainingFoodCount = sItem.getCustomData(Man10Fishing.instance, "foodCount").toInt()

            currentFood = sItem.getCustomData(Man10Fishing.instance, "foodType").split("|").map { it.toDouble() }
            season=Season.stringToSeason(sItem.getCustomData(Man10Fishing.instance,"season")?:"NONE")
        }
    }

    //食べ物回数

    fun getFoodCount(): Int {
        return remainingFoodCount
    }

    fun setFoodCount(count: Int){
        remainingFoodCount = count
        if(remainingFoodCount < 0) remainingFoodCount = 0
        SItemStack(rodItem).setCustomData(Man10Fishing.instance, "foodCount", remainingFoodCount.toString())
    }

    fun addFoodCount(count: Int){
        setFoodCount(getFoodCount() + count)
    }

    fun removeFoodCount(count: Int): Boolean {
        setFoodCount(getFoodCount() - count)
        return getFoodCount() > 0
    }

    fun useFood(player:Player):Boolean{

        if(remainingFoodCount<foodPerUse)return false

        if(Random.nextDouble()<savingChance)player.playSound(player.location,Sound.BLOCK_NOTE_BLOCK_BELL,0.5F,2.0F)
        else removeFoodCount(foodPerUse)

        return true
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

    fun setFoodType(foodString: String){
        val result = ArrayList<Double>()
        val foodSeparatedInformation = foodString.split("|")
        if(foodSeparatedInformation.size != 6){
            return
        }
        for(foodInfo in foodSeparatedInformation){
            if(!BaseUtils.isDouble(foodInfo)){
                return
            }
            result.add(foodInfo.toDouble())
        }
        currentFood=result
        rodItem = SItemStack(rodItem).setCustomData(Man10Fishing.instance, "foodType", foodString).build()
    }

    fun getFoodType(): List<Double>{
        return currentFood
    }

    //最後に餌の情報が書かれていることを前提としている
    fun updateLore(){
        val count=getFoodCount()

        val newRod=SItemStack(rodItem)
        val oldLore=newRod.lore
        var loreNum=oldLore.size
        if(oldLore.contains("§c餌未設定")){
            loreNum=oldLore.indexOf("§c餌未設定")
        }
        else if(oldLore.contains("§aセット中の餌")){
            loreNum=oldLore.indexOf("§aセット中の餌")
        }

        val newLore= mutableListOf<String>()
        newLore.addAll(oldLore.subList(0,loreNum))
        if(count<1){
            newLore.add("§c餌未設定")
        }
        else{
            newLore.add("§aセット中の餌")
            newLore.addAll(FishFood.getFoodTypeLore(getFoodType()))
            newLore.add("§e残り§b${getFoodCount()}§e個")
        }
        rodItem=SItemStack(rodItem).setLore(newLore).build()
    }
}