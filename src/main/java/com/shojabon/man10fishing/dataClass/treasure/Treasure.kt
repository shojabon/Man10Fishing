package com.shojabon.man10fishing.dataClass.treasure

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.enums.TreasureRank
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class Treasure(val name:String,val config:ConfigurationSection){

    private var item: ItemStack = ItemStack(Material.DARK_OAK_PLANKS)
    private var alias=""

    init {
        loadConfig()
    }

    val rank=try {
        TreasureRank.valueOf(config.getString("rank","")!!)
    }catch (e:Exception){
        TreasureRank.ERROR
    }

    fun getItem():ItemStack{
        return item.clone()
    }

    private fun loadConfig(): String? {
        alias = config.getString("alias")?: return "不正alias"
        val material = Material.getMaterial(config.getString("item.material")?: return "不正マテリアル")?: return "不正マテリアル"
        val customModelData = config.getInt("item.customModelData")
        val lore = config.getStringList("item.lore")

        val itemStack = SItemStack(material).setDisplayName(alias)
        itemStack.customModelData = customModelData
        itemStack.lore = lore

        item = itemStack.build()?: return "不正アイテム"
        val itemMeta=item.itemMeta
        itemMeta.persistentDataContainer.set(NamespacedKey(Man10Fishing.instance,"treasure"), PersistentDataType.STRING,name)

        item.itemMeta=itemMeta

        return null
    }



}

