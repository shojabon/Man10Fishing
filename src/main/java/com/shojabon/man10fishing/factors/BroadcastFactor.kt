package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import com.shojabon.man10fishing.Man10FishingAPI
import org.bukkit.Material
import com.shojabon.man10fishing.dataClass.FishFactor
import org.bukkit.entity.Player
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import org.bukkit.Bukkit
import kotlin.math.pow

@FishFactorDefinition(name = "全体通知設定",
        iconMaterial = Material.BELL,
        explanation = ["大物を釣りが手たときの通知設定"],
        adminSetting = false,
        settable = false)
class BroadcastFactor(fish: Fish) : FishFactor(fish) {

    var enabled = FishSettingVariable("broadcast.enabled", false)

    override fun onFish(fish: Fish, fisher: Player, rod: FishingRod) {
        if(!enabled.get()) return
        Bukkit.broadcastMessage(Man10Fishing.prefix + "§e§l" + fisher.name + "は" + fish.alias + "§e§lを釣り上げた！")
    }

}