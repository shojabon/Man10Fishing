package com.shojabon.man10fishing.itemindex

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

class ItemIndexCategory(val plugin: JavaPlugin, val uuid : UUID) : LargeSInventoryMenu("レアリティ一覧",plugin) {


    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        for (rarity in Man10FishingAPI.rarity){
            items.add(SInventoryItem(SItemStack(rarity.value.material).setDisplayName(rarity.value.alias).build()).clickable(false)
                .setEvent { ItemIndexInventory(rarity.key,plugin,uuid,true).open(Bukkit.getPlayer(uuid)) })
        }

        setItems(items)
    }
}