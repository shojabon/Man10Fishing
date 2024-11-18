package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "レコード",
        iconMaterial = Material.MUSIC_DISC_11,
        explanation = ["サーバー全体における魚のレコードに関するロジック"],
        settable = false)
class RecordFactor(fish : Fish) : FishFactor(fish) {

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        Man10FishingAPI.fishRecords[fish.name]?.let {
            if(it.maxsize<parameter.size){
                it.maxsize=parameter.size
                it.maxUuid=fisher.uniqueId
                Man10Fishing.api.broadcastPlMessage("§d§lサーバーレコード更新!")
                Man10Fishing.api.broadcastPlMessage("§cレコード: §eサーバ内最大記録")
                Man10Fishing.api.broadcastPlMessage("§c魚：§e${fish.alias}")
                Man10Fishing.api.broadcastPlMessage("§c釣った人：§e${fisher.name}")
                Man10Fishing.api.broadcastPlMessage("§cサイズ：§e${parameter.size}cm")
            }
            if(it.minsize>parameter.size){
                it.minsize=parameter.size
                it.minUuid=fisher.uniqueId
                Man10Fishing.api.broadcastPlMessage("§d§lサーバーレコード更新!")
                Man10Fishing.api.broadcastPlMessage("§cレコード: §eサーバ内最小記録")
                Man10Fishing.api.broadcastPlMessage("§c魚：§e${fish.alias}")
                Man10Fishing.api.broadcastPlMessage("§c釣った人：§e${fisher.name}")
                Man10Fishing.api.broadcastPlMessage("§cサイズ：§e${parameter.size}cm")
            }
            it.amount++
        }?: kotlin.run { Man10FishingAPI.fishRecords[fish.name]=
                FishRecordData(fisher.uniqueId,parameter.size,fisher.uniqueId,parameter.size,1,fisher.uniqueId)

            Man10Fishing.api.broadcastPlMessage("§aなんと！§e${fisher.name}§aが新種${Man10FishingAPI.rarity[fish.rarity]?.namePrefix}§l${parameter.fish.alias}§aを釣り上げた！")

        }
    }
}