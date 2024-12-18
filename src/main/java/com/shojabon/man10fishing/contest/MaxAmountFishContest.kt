package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit

/*

該当の魚を釣った数を競うコンテスト
数が同じである場合、先にその数を達成した方を上位とする

*/
class MaxAmountFishContest:AbstractFishContest() {



    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty() &&!targetFishList.contains(fish.fish.name))return

        player.addAllowedCaughtFish(fish)

        broadCastPlayers("${player.name}§aが§e${fish.fish.alias}§aを釣り上げた!")
    }

    override fun onStart() {


        bossBar.setTitle("§e§l最も多く§c§l${targetFishName}§e§lを釣れ！")

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

        broadCastPlayers("§c§l順位")
        //サブスレッドで実行されてると思ってる
        Thread.sleep(500)
        for(i in 1 until visibleRankingSize+1){
            val data=ranking[i]?:continue
            broadCastPlayers("§a${i}位: §e${data.name}§7:§b${data.allowedCaughtFish.size}匹")
        }
    }

    override fun applyAdditionalPlaceHolder(str: String, contestPayer: FishContestPlayer): String {
        return str.replace("<count>",contestPayer.allowedCaughtFish.size.toString())
    }

    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer):Boolean{
        return lowerPlayer.allowedCaughtFish.size<=higherPlayer.allowedCaughtFish.size
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.size}匹"
    }

}