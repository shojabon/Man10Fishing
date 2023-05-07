package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.itemindex.ItemIndex
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "tororo_1066")
@FishFactorDefinition(name = "図鑑",
    iconMaterial = Material.BOOK,
    explanation = ["図鑑に保存するロジック"],
    settable = false)
class ItemIndexFactor(fish : Fish) : FishFactor(fish){

    val index = FishSettingVariable("index",-1)

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        if (index.get() == -1)return

        val fishdexList = ItemIndex.fishdexList

        if (!fishdexList.containsKey(fisher.uniqueId)){
            fishdexList[fisher.uniqueId] = hashMapOf()
        }

        val playerData = fishdexList[fisher.uniqueId]

        if (!playerData!!.containsKey(fish.name)) {
            playerData[fish.name] = arrayListOf()
        }

        playerData[fish.name]!!.add(parameter)
    }


}