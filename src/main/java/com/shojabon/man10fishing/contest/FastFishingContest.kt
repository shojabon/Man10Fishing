package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player

class FastFishingContest:AbstractFishContest() {

    private val targetFishList=config.getList("targetFishes")
    private val targetFishName=config.getString("targetFishName","魚")
    private val targetFishAmount=config.getInt("amount")

    private var winningPlayerLimit=config.getInt("winnerPlayerLimit", 3)

    //rewardCommandsの名前はここだけ変えた方が良いかもしれない
    private var rewardCommands =config.getStringList("rewardCommands")
    private var winner:Player?=null

    private val bossBar = Bukkit.createBossBar("§e§l一番はじめに&c&l${targetFishName}§e§lを§c§l${targetFishAmount}匹§e§l釣れ！", BarColor.BLUE, BarStyle.SOLID)


    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {
        if(targetFishList!=null&&!targetFishList.contains(fish.fish.name))return

        //このstopがonEndを実行するかどうか未確認
        time.stop()
    }

    override fun onStart() {
        TODO("Not yet implemented")
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
        rewardCommands.forEach {
            dispatchCommand(it
                    .replace("<player>",winner!!.name)
                    .replace("<uuid>",winner!!.uniqueId.toString())
                    .replace("<world>",winner!!.world.name))
        }
    }
}