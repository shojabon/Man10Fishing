package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MaxSumSizeFishContest:AbstractFishContest() {


    //configからとるもの


    private var winner: Player?=null

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty()&&!targetFishList.contains(fish.fish.name))return

        player.addAllowedCaughtFish(fish)

        Bukkit.getPlayer(player.uuid)?.sendMessage("§c現在のサイズ合計：§e${player.allowedCaughtFish.sumOf { it.size }}cm")


    }

    override fun onStart() {

        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!


        bossBar.setTitle("§e§l大きな§c§l${targetFishName}§e§lをたくさん釣れ！")

        time.setRemainingTime(config.getInt("time", 60))
    }

    override fun onEnd() {

        broadCastPlayers("§c§lコンテスト終了!!")

        //サブスレッドで実行されてると思ってる
        Thread.sleep(4000)

        if(ranking.isEmpty()){
            broadCastPlayers("§c§l魚を釣ったプレイヤーはいませんでした")
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
        return lowerPlayer.allowedCaughtFish.sumOf { it.size }<=higherPlayer.allowedCaughtFish.sumOf { it.size }
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.sumOf { it.size }}cm"
    }
}