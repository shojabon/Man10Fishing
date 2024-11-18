package com.shojabon.man10fishing.itemindex.inventory

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

class ItemIndexCategory(val plugin: JavaPlugin, val uuid : UUID) : LargeSInventoryMenu("レアリティ一覧",plugin) {


    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        for (itemIndex in ItemIndex.itemIndexes.values){
            if(itemIndex.hidden){
                var flag=true
                itemIndex.fish.forEach {
                    if(ItemIndex.fishdexList[uuid]?.containsKey(it)?:true){
                        flag=false
                        return@forEach
                    }
                }
                if(flag)continue
            }
            items.add(SInventoryItem(SItemStack(itemIndex.displayItem).setDisplayName(itemIndex.displayName).build()).clickable(false)
                .setEvent { ItemIndexInventory(plugin,itemIndex.displayName,itemIndex,uuid,true).open(Bukkit.getPlayer(uuid)) })
        }

        setItems(items)
    }
}