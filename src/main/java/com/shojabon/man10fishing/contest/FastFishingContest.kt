package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class FastFishingContest:AbstractFishContest() {


    //configからとるもの
    private lateinit var targetFishList:List<String>
    private var targetFishName=""
    private var targetFishAmount=1
    private var winningPlayerLimit=3


    private var winner:Player?=null

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty()&&!targetFishList.contains(fish.fish.name))return
        player.addAllowedCaughtFish(fish)

        broadCastPlayers("§f${player.name}§aが§e${players[player.uuid]?.allowedCaughtFish?.size}匹目§aの${targetFishName}を釣り上げた!")

        if(player.allowedCaughtFish.size >= targetFishAmount){
            winner=Bukkit.getPlayer(player.uuid)
            end()
        }
    }

    override fun onStart() {
        targetFishList=config.getStringList("targetFishes")
        targetFishName= config.getString("targetFishName","魚")!!
        targetFishAmount=config.getInt("amount",1)
        winningPlayerLimit=config.getInt("winnerPlayerLimit", 3)

        bossBar.setTitle("§e§l一番はじめに§c§l${targetFishName}§e§lを§c§l${targetFishAmount}匹§e§l釣れ！")

        time.setRemainingTime(config.getInt("time", 60))
    }

    override fun onEnd() {

        broadCastPlayers("§c§lコンテスト終了!!")

        //サブスレッドで実行されてると思ってる
        Thread.sleep(4000)

        if(winner==null){
            broadCastPlayers("§c§l${targetFishName}を${targetFishAmount}匹釣ったプレイヤーはいませんでした")
            return
        }
        broadCastPlayers("§c§l達成者：§e${winner!!.name}")
        Thread.sleep(1000)
        broadCastPlayers("§c§lおめでとうございます!!")
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.size}匹"
    }
}