package com.shojabon.man10fishing.itemindex

import ToolMenu.LargeSInventoryMenu
import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import com.shojabon.mcutils.Utils.SItemStack
import com.shojabon.mcutils.Utils.SStringBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*
import kotlin.contracts.contract
import kotlin.math.abs


class ItemIndexInventory(private val rarityName : String, private val plugin: JavaPlugin, private val uuid : UUID, private val fromCategory : Boolean) : LargeSInventoryMenu(rarityName, plugin) {



    override fun renderMenu() {
        val items = ArrayList<SInventoryItem>()

        val fishdexList = ItemIndex.fishdexList[uuid]?.filter {
            if (rarityName == "all") true else {
                it.value.firstOrNull()?.fish?.rarity == rarityName
            }
        }
        if (fishdexList.isNullOrEmpty()){
            Bukkit.getPlayer(uuid)?.sendMessage(Man10Fishing.prefix + "§4図鑑情報がありません")
            return
        }

        for (fish in Man10FishingAPI.fish.filter {
            if (rarityName == "all") true else {
                it.value.rarity == rarityName
            }
        }.filter { getFishIndex(it.value) != -1 }.entries.sortedBy { getFishIndex(it.value) }){
            items.add(SInventoryItem(SItemStack(Material.GLASS_PANE).setDisplayName("${getFishIndex(fish.value)}").build()).clickable(false))
        }

        for (fishdex in fishdexList){
            if (fishdex.value.firstOrNull()?.loaded == false)continue
            val index = getFishIndex(fishdex.value.first())
            if (index == -1)continue
            val oneData = fishdex.value.maxByOrNull { it.size }!!
            val item = (oneData.generateIndexItem()?.clickable(false)?:continue).setEvent { changeMoreInfoItem(it.slot,oneData) }
            items[index] = item
        }

        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.BLOCK_CHEST_OPEN,1f,2f) }
        setItems(items)

        setOnCloseEvent {
            if (fromCategory){
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    val player = Bukkit.getPlayer(uuid)?:return@Runnable
                    movingPlayer.add(player.uniqueId)
                    ItemIndexCategory(plugin,uuid).open(player)
                    movingPlayer.remove(player.uniqueId)
                })
            }
        }
    }


    override fun afterRenderMenu() {
        renderInventory(0)
    }


    private fun getFishIndex(fishdex: FishParameter): Int {
        return fishdex.fish.config.getInt("fishFactors.index", -1)
    }

    private fun getFishIndex(fishdex: Fish): Int {
        return fishdex.config.getInt("fishFactors.index", -1)
    }

    private fun changeMoreInfoItem(slot : Int, parameter: FishParameter){
        Bukkit.getPlayer(uuid)?.location?.let { Bukkit.getPlayer(uuid)?.playSound(it, Sound.ENTITY_PLAYER_LEVELUP,1f,1.5f) }
        val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(parameter.dateTime)
        val item = SItemStack((parameter.generateIndexItem()?:return).itemStack.clone())
        item.lore = mutableListOf("§d長さ：${parameter.size}cm","§6釣れた日：${sdFormat}")
        val foodList = parameter.fish.config.getString("fishFactors.food.matrix")!!.split(",").map { it.toDouble() }
        if (!parameter.fish.config.getBoolean("fishFactors.food.hide")){
            item.lore.addAll(FishFood.getFoodTypeLore(foodList))
        }
        item.addLore(" ")
        item.addLore(Man10FishingAPI.rarity[parameter.fish.rarity]!!.loreDisplayName)

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