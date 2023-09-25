package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "tororo_1066")
@FishFactorDefinition(name = "エリア",
    iconMaterial = Material.OAK_FENCE_GATE,
    explanation = ["釣れるエリア設定(WorldGuard)"],
    adminSetting = false,
    settable = true)
/**
 * エリアロジック
 * area: <エリア名>
 * none(無記述)で無効化できます
 */
class AreaFactor(fish : Fish) : FishFactor(fish){

    val areas = FishSettingVariable("area",ArrayList<String>(mutableListOf("none")))

     override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod, hookLocation: Location): Boolean {
         if (areas.get().contains("none"))return true
         if (Man10Fishing.regionContainer == null)return false
         for (region in Man10Fishing.regionContainer!![BukkitAdapter.adapt(fisher.world)]!!.regions){
             for (area in areas.get()){
                 if (region.key.startsWith(area)){
                     if (region.value.contains(BukkitAdapter.asBlockVector(hookLocation)))return true
                 }
             }
         }
         return false
    }
}