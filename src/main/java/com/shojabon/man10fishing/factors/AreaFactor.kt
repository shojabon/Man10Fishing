package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import com.shojabon.man10fishing.dataClass.FishingRod
import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "tororo_1066")
@FishFactorDefinition(name = "エリア",
    iconMaterial = Material.OAK_FENCE_GATE,
    explanation = ["エリアロジックの定義"],
    settable = false)
class AreaFactor(fish : Fish) : FishFactor(fish){

    val area = FishSettingVariable("area","none")

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {
        if (area.get() == "none")return true
        val region = Man10Fishing.regionContainer[BukkitAdapter.adapt(fisher.world)]?.getRegion(area.get())?:return false
        return region.contains(BukkitAdapter.asBlockVector(fisher.location))
    }
}