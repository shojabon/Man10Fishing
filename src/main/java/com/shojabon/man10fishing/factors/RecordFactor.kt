package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
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
                val text=Component.text("§cレコード：§eサーバー内最大記録")
                        .appendNewline().append(Component.text("§c釣った人：§e${fisher.player?.name}"))
                        .appendNewline().append(Component.text("§cサイズ：§e${parameter.size}cm"))
                Man10Fishing.instance.server.broadcast(
                        Component.text("${Man10Fishing.prefix}§d${fish.alias}の§c§nサーバーレコード§dが更新されました")
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,text))
                )

            }
            if(it.minsize>parameter.size){
                it.minsize=parameter.size
                it.minUuid=fisher.uniqueId
                val text=Component.text("§cレコード：§eサーバー内最小記録")
                        .appendNewline().append(Component.text("§c釣った人：§e${fisher.player?.name}"))
                        .appendNewline().append(Component.text("§cサイズ：§e${parameter.size}cm"))
                Man10Fishing.instance.server.broadcast(
                        Component.text("${Man10Fishing.prefix}§d${fish.alias}の§c§nサーバーレコード§dが更新されました")
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,text))
                )
            }
            it.amount++
        }?: kotlin.run { Man10FishingAPI.fishRecords[fish.name]=
                FishRecordData(fisher.uniqueId,parameter.size,fisher.uniqueId,parameter.size,1,fisher.uniqueId)

            Man10Fishing.api.broadcastPlMessage("§aなんと！§e${fisher.name}§aが新種${Man10FishingAPI.rarity[fish.rarity]?.namePrefix}§l${parameter.fish.alias}§aを釣り上げた！")

        }
    }
}