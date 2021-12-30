package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "tororo_1066")
@FishFactorDefinition(name = "エリア",
    iconMaterial = Material.OAK_FENCE_GATE,
    explanation = ["エリア指定の定義"],
    adminSetting = false,
    settable = true)
/**
 * エリアロジック
 * area: <エリア名>
 * none(無記述)で無効化できます
 */
class AreaFactor(fish : Fish) : FishFactor(fish){

    val areas = FishSettingVariable("area",ArrayList<String>(mutableListOf("none")))

     override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {
        if (areas.get().contains("none"))return true
        for (area in areas.get()){
            val region = Man10Fishing.regionContainer[BukkitAdapter.adapt(fisher.world)]?.getRegion(area)?:return false
            if (!region.contains(BukkitAdapter.asBlockVector(fisher.location)))continue
            return true
        }
        return false
    }
}