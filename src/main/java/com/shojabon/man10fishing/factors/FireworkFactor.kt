package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.dataClass.enums.SizeRank
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player

@FishFactorDefinition(name = "花火",
        iconMaterial = Material.FIREWORK_ROCKET,
        explanation = ["花火による演出を行うかどうかの設定"],
        adminSetting = false,
        settable = false)
class FireworkFactor(fish: Fish) : FishFactor(fish) {
    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        if(Man10FishingAPI.rarity[parameter.fish.rarity]!!.firework){
            Man10Fishing.instance.server.scheduler.runTask(Man10Fishing.instance, Runnable {
                val fw=fisher.world.spawnEntity(fisher.location, EntityType.FIREWORK) as Firework
                val meta=fw.fireworkMeta
                meta.power=1
                val eff= FireworkEffect.builder().withColor(Color.LIME).withColor(Color.WHITE).withColor(Color.YELLOW)
                        .with(FireworkEffect.Type.BALL_LARGE).flicker(true).build()
                meta.addEffect(eff)
                fw.fireworkMeta=meta
            })
        }
    }
}