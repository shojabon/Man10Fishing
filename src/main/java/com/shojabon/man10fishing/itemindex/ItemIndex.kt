package com.shojabon.man10fishing.itemindex

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.mcutils.Utils.SConfigFile
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ItemIndex {

    companion object {
        val fishdexList = HashMap<UUID,HashMap<String,ArrayList<FishParameter>>>()
        val itemIndexes = HashMap<String, ItemIndex>()

        fun fromConfig(file: File): ItemIndex {
            val itemIndex = ItemIndex()
            val config = YamlConfiguration.loadConfiguration(file)
            itemIndex.internalName = file.nameWithoutExtension
            itemIndex.displayName = config.getString("displayName")?:""
            itemIndex.displayItem = Material.getMaterial(config.getString("displayItem")?:"STONE")!!
            itemIndex.onCompleteItemStack = config.getItemStack("onCompleteItemStack")
            itemIndex.onCompleteCommands.addAll(config.getStringList("onCompleteCommands"))
            itemIndex.completedPlayers.addAll(config.getStringList("completedPlayers").map { UUID.fromString(it) })
            itemIndex.fish.addAll(config.getStringList("fish"))
            return itemIndex
        }

        fun fromRarity(rarity: FishRarity): ItemIndex{
            val itemIndex = ItemIndex()
            itemIndex.internalName = rarity.name
            itemIndex.displayName = rarity.loreDisplayName
            itemIndex.displayItem = rarity.material
            itemIndex.fish.addAll(ArrayList(Man10FishingAPI.fish.filter { it.value.rarity == rarity.name }.keys))
            itemIndex.fromRarity = true
            return itemIndex
        }
    }

    var internalName = ""
    var displayName = "図鑑"
    var displayItem: Material = Material.STONE
    var onCompleteItemStack: ItemStack? = null
    val onCompleteCommands = ArrayList<String>()
    val completedPlayers = ArrayList<UUID>()
    val fish = ArrayList<String>()

    var fromRarity = false

    fun save(){
        val config = SConfigFile.getConfigFile(Man10Fishing.instance.dataFolder.path + "/itemIndexes/${internalName}.yml")
                ?:YamlConfiguration()
        config.set("displayName", displayName)
        config.set("displayItem", displayItem.name)
        config.set("onCompleteItemStack", onCompleteItemStack)
        config.set("onCompleteCommands", onCompleteCommands)
        config.set("completedPlayers", completedPlayers.map { it.toString() })
        config.set("fish", fish)
        val file = File(Man10Fishing.instance.dataFolder.path + "/itemIndexes/${internalName}.yml")
        if (!file.exists()) file.createNewFile()
        config.save(file)
    }
}