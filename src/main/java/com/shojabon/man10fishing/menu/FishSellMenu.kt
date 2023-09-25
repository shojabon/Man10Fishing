package com.shojabon.man10fishing.menu

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material

class FishSellMenu: SInventory("§b魚を売る", 4, Man10Fishing.instance) {

    init {
        setOnClickEvent {
            it.isCancelled = true
        }
        setItem((27..35).toList().toIntArray(), SInventoryItem(SItemStack(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ").build()).clickable(false))

        setItem((30..32).toList().toIntArray(), SInventoryItem(SItemStack(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§l売却").build()).clickable(false).setAsyncEvent {
            val fish = it.inventory
        })
    }

    fun getSellPrice(fish: FishParameter): Double {
        val minSize = fish.fish.sizeFactor.min.get()
        val maxSize = fish.fish.sizeFactor.max.get()
        val size = fish.size
        val rarity = Man10FishingAPI.rarity[fish.fish.rarity]?:return 0.0
        //sizeが大きさの幅の何%にあるか
        val sizePercentage = (size - minSize) / (maxSize - minSize)
        return rarity.minSellPrice + (rarity.priceMultiplier * sizePercentage)
    }
}