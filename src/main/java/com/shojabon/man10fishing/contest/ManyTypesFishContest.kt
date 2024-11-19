package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ManyTypesFishContest:AbstractFishContest() {


    //configからとるもの

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(player.allowedCaughtFish.find { it.fish.alias==fish.fish.alias }!=null)return

        player.addAllowedCaughtFish(fish)

    }

    override fun onStart() {

        bossBar.setTitle("§e§lたくさんの種類の${targetFishName}§e§lを釣れ！")

        time.setRemainingTime(config.getInt("time", 60))
    }

    override fun onEnd() {

        broadCastPlayers("§c§lコンテスト終了!!")

        //サブスレッドで実行されてると思ってる
        Thread.sleep(4000)

        if(ranking.isEmpty()){
            broadCastPlayers("§c§l${targetFishName}を釣ったプレイヤーはいませんでした")
            return
        }


        for(i in 1 until visibleRankingSize+1){
            val data=ranking[i]?:continue
            broadCastPlayers("§a${i}位: §e${data.name}§7:§b${data.allowedCaughtFish.size}匹")
        }

    }

    override fun applyAdditionalPlaceHolder(str: String, contestPayer: FishContestPlayer): String {
        return str.replace("<count>",contestPayer.allowedCaughtFish.size.toString())
    }

    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        return lowerPlayer.allowedCaughtFish.size<=higherPlayer.allowedCaughtFish.size
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.size}種類"
    }

}