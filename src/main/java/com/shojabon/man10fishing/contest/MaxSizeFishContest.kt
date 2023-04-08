package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server

class MaxSizeFishContest: AbstractFishContest() {

    var maxSize: Pair<FishContestPlayer, FishParameter>? = null

    override fun onStart() {

    }

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {
        if (maxSize == null) {
            maxSize = Pair(player, fish)
            broadCastPlayers("§e§l${player.name}が最大サイズの§a§l${fish.fish.alias}§e§lを釣り上げた！§d§l(${fish.size}cm)")
            return
        }

        if (maxSize!!.second.size < fish.size) {
            maxSize = Pair(player, fish)
            broadCastPlayers("§e§l${player.name}が最大サイズの§a§l${fish.fish.alias}§e§lを釣り上げた！§d§l(${fish.size}cm)")
        }
    }

    override fun onEnd() {
        if (maxSize == null) {
            broadCastPlayers("§c§l魚が一匹も釣られませんでした")
            return
        }

        broadCastPlayers("§e§l${maxSize!!.first.name}が最大サイズの§a§l${maxSize!!.second.fish.alias}§e§lを釣り上げた！§d§l(${maxSize!!.second.size}cm)")
    }
}