package com.shojabon.man10fishing.itemindex

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*


class ItemIndexInventory(private val rarityName : String, plugin: JavaPlugin, private val uuid : UUID) : LargeSInventoryMenu(rarityName, plugin) {



    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        val fishdexList = ItemIndex.fishdexList[uuid]?.filter { it.value.fish.rarity == rarityName }
        if (fishdexList == null){
            Bukkit.getPlayer(uuid)?.sendMessage(Man10Fishing.prefix + "§4図鑑情報がありません")
            return
        }


        val lastId = fishdexList.entries.maxByOrNull { it.value.fish.config.getInt("fishFactors.index") }!!.value.fish.config.getInt("fishFactors.index")


        for (loop in 0..lastId){
            items.add(SInventoryItem(SItemStack(Material.GLASS_PANE).setDisplayName("$loop").build()).clickable(false))
        }

        for (fishdex in fishdexList){
            if (!fishdex.value.loaded)continue
            val index = getFishIndex(fishdex.value)
            if (index == -1)continue
            val item = (fishdex.value.generateIndexItem()?.clickable(false) ?:continue).setEvent { changeMoreInfoItem(it.slot,fishdex.value) }
            items[index] = item
        }

        setItems(items)
    }


    override fun afterRenderMenu() {
        renderInventory(0)
    }

    private fun getFishIndex(fishdex: FishParameter): Int {
        return fishdex.fish.config.getInt("fishFactors.index", -1)
    }

    private fun changeMoreInfoItem(slot : Int, parameter: FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(parameter.dateTime)
        val item = SItemStack((parameter.generateIndexItem()?:return).itemStack.clone())
        item.lore = mutableListOf("§d大きさ：${parameter.size}g","§b長さ：${parameter.weight}cm","§6釣れた日：${sdFormat}")

        setItem(slot, SInventoryItem(item.build()).clickable(false).setEvent { changeSoftInfoItem(it.slot,parameter) })
        renderInventory()
    }

    private fun changeSoftInfoItem(slot : Int, parameter: FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val item = parameter.generateIndexItem()?:return
        setItem(slot, item.clickable(false).setEvent { changeMoreInfoItem(it.slot,parameter) })
        renderInventory()
    }
}