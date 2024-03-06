package com.shojabon.man10fishing.menu

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TreasureBoxMenu: SInventory("§e宝箱", 4, Man10Fishing.instance) {





    init {
        setOnClickEvent{
            it.isCancelled=true
        }
    }

    override fun renderMenu() {

        fillItem(SItemStack(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("").build())



    }



}