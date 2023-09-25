package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.dataClass.enums.SizeRank
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
        val rarityData= Man10FishingAPI.rarity[parameter.fish.rarity]!!
        when(parameter.sizeRank){
            SizeRank.BIG->{
                Man10Fishing.api.broadcastPlMessage("§f§l${fisher.name}§aが§e§l巨大サイズ§aの${rarityData.namePrefix}§l${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§aを釣り上げた!")
                return
            }
            SizeRank.SMALL->{
                Man10Fishing.api.broadcastPlMessage("§f§l${fisher.name}§aが§e§lミニサイズ§aの${rarityData.namePrefix}§l${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§aを釣り上げた!")
                return
            }

            else->{}
        }
        if(enabled.get()){
            Bukkit.broadcast(Component.text(Man10Fishing.prefix + "§f§l${fisher.name}が${rarityData.namePrefix}${rarityData.loreDisplayName} §l${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§f§lを釣り上げた!"), Server.BROADCAST_CHANNEL_USERS)
            return
        }
        fisher.sendMessage(Man10Fishing.prefix+"${rarityData.namePrefix}§l${parameter.fish.alias}§e(§f${parameter.size}cm§e)§f§lを釣り上げた!")
    }

}