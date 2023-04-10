package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.Material
import org.bukkit.entity.Player
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server

@FishFactorDefinition(name = "全体通知設定",
        iconMaterial = Material.BELL,
        explanation = ["大物を釣りが手たときの通知設定"],
        adminSetting = false,
        settable = false)
class BroadcastFactor(fish: Fish) : FishFactor(fish) {

    var enabled = FishSettingVariable("broadcast.enabled", false)

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        if(!enabled.get()) return
        Bukkit.broadcast(Component.text(Man10Fishing.prefix + "§e§l" + fisher.name + "は" + fish.alias + "§e§lを釣り上げた！"), Server.BROADCAST_CHANNEL_USERS)
    }

}