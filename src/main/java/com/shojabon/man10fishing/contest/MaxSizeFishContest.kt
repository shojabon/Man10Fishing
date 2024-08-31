package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit

class MaxSizeFishContest: AbstractFishContest() {


    override fun onStart() {
        time.setRemainingTime(config.getInt("time", 60))
        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!

        bossBar.setTitle("§e§l最も大きい${targetFishName}を釣れ！")
    }

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty() &&!targetFishList.contains(fish.fish.name))return

        if(player.allowedCaughtFish.isEmpty()){
            player.addAllowedCaughtFish(fish)
        }
        else if (player.allowedCaughtFish[0].size < fish.size){
            player.allowedCaughtFish.removeFirstOrNull()
            player.addAllowedCaughtFish(fish)
        }

        val max = players.values.mapNotNull { it.allowedCaughtFish.firstOrNull()?.size }.maxOrNull()
        if (max != null && max == fish.size){
            bossBar.setTitle("§e§l最も大きい魚を釣れ！§b${fish.fish.alias}§d(${fish.size}cm)")
        }

    }

    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        if (lowerPlayer.allowedCaughtFish.isEmpty()){
            return higherPlayer.allowedCaughtFish.isNotEmpty()
        }

        if (higherPlayer.allowedCaughtFish.isEmpty()){
            return false
        }

        return lowerPlayer.allowedCaughtFish.first().size <= higherPlayer.allowedCaughtFish.first().size
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.first().size}cm"
    }

    override fun onEnd() {
        broadCastPlayers("§c§lコンテスト終了!!")

        Thread.sleep(4000)

        if (ranking.isEmpty()) {
            broadCastPlayers("§c§l${targetFishName}を釣ったプレイヤーはいませんでした")
            return
        }

        broadCastPlayers("§c§l順位")

        Thread.sleep(500)

        ranking.forEach { (i, data) ->
            val fish = data.allowedCaughtFish.firstOrNull()?:return@forEach
            broadCastPlayers("§a${i}位: §e${data.name}§7:" +
                    "§b${fish.fish.alias}" +
                    "§d(${fish.size}cm)")
        }
    }

    override fun applyAdditionalPlaceHolder(str: String, contestPayer: FishContestPlayer): String {
        val fish=contestPayer.getMaxSizeAllowedFish()
        return str.replace("<fish>",fish?.name?:"")
                .replace("<size>",fish?.size?.toString()?:"")
    }
}