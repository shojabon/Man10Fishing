package com.shojabon.man10fishing.itemindex

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class ItemIndexInventory(private val rarityName : String, plugin: JavaPlugin, private val uuid : UUID) : LargeSInventoryMenu(rarityName, plugin) {



    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        val fishdexList = ItemIndex.fishdexList[uuid]
        if (fishdexList == null){
            Bukkit.getPlayer(uuid)?.sendMessage(Man10Fishing.prefix + "図鑑情報がありません")
            return
        }


        val lastId = fishdexList.entries.maxByOrNull { it.value.fish.config.getInt("fishFactors.index") }!!.value.fish.config.getInt("fishFactors.index")


        for (loop in 0..lastId){
            items.add(SInventoryItem(SItemStack(Material.GLASS_PANE).setDisplayName("$loop").build()).clickable(false))
        }

        for (fishdex in fishdexList){
            if (!fishdex.value.loaded)continue
            if (fishdex.value.fish.rarity != rarityName)continue
            val index = getFishIndex(fishdex.value)
            if (index == -1)continue
            fishdex.value.generateIndexItem()?.clickable(false)?.let { items.set(index, it) }
        }

        setItems(items)
    }

    override fun afterRenderMenu() {
        renderInventory(0)
    }

    private fun getFishIndex(fishdex: FishParameter): Int {
        return fishdex.fish.config.getInt("fishFactors.index", -1)
    }
}