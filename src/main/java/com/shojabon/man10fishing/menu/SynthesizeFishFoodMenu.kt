package com.shojabon.man10fishing.menu

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer


class SynthesizeFishFoodMenu: SInventory("§6§l餌合成", 4, Man10Fishing.instance){

    var firstTimeCraft = true

    init {
        this.fillItem(SInventoryItem(SItemStack(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))
        this.setItem(11, ItemStack(Material.AIR))
        this.setItem(15, ItemStack(Material.AIR))
        //this.setItem(22, ItemStack(Material.AIR))
        this.setItem(intArrayOf(30, 31, 32), SInventoryItem(SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l決定").build()).clickable(false).setEvent(this.onConfirm()))
        this.setOnClickEvent(this.onClick())
    }

    fun onConfirm(): Consumer<InventoryClickEvent> {
        return Consumer<InventoryClickEvent> {
            val item1 = this.activeInventory.getItem(11)
            val item2 = this.activeInventory.getItem(15)
            if(item1 == null || item2 == null){
                it.whoClicked.sendMessage(Man10Fishing.prefix + "§4§l餌は2種類ないと合成できません");
                return@Consumer
            }
            if(this.activeInventory.getItem(22) != null && !this.firstTimeCraft){
                it.whoClicked.sendMessage(Man10Fishing.prefix + "§4§l完成台からアイテムを外してください");
                return@Consumer
            }
            val food1 = FishFood(item1)
            val food2 = FishFood(item2)
            val resultFood = this.getSynthesizedFood(food1, food2) ?: return@Consumer
            this.setItem(22, resultFood.food)
            this.setItem(11, ItemStack(Material.AIR))
            this.setItem(15, ItemStack(Material.AIR))
            this.renderInventory()
            this.firstTimeCraft = false
        }
    }

    fun onClick(): Consumer<InventoryClickEvent> {
        return Consumer<InventoryClickEvent> {
            if(it.currentItem == null){
                it.isCancelled = true
                return@Consumer
            }
            if(!FishFood.isFood(it.currentItem!!)){
                it.isCancelled = true
                return@Consumer
            }
        }
    }

    fun getFinalAmount(food1: FishFood, food2: FishFood): Int {
        return (food1.food.amount + food2.food.amount)/2
    }

    fun getSynthesizedFood(food1: FishFood, food2: FishFood): FishFood? {


        val mixedType = FishFood.mixFoodType(food1, food2) ?: return null
        val lore=FishFood.getFoodTypeLore(mixedType)

        val item = SItemStack(Material.FIREWORK_STAR).setLore(lore)
        item.amount = this.getFinalAmount(food1, food2)

        val result = FishFood(item.build())

        result.setFoodTypeList(mixedType)

        return result
    }

}