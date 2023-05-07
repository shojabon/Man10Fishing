package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class FastFishingContest:AbstractFishContest() {


    //configからとるもの
    private lateinit var targetFishList:List<String>
    private var targetFishName=""
    private var targetFishAmount=1
    private var winningPlayerLimit=3
    //rewardCommandsの名前はここだけ変えた方が良いかもしれない
    private var rewardCommands:List<String>?=null


    private var winner:Player?=null

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty()&&!targetFishList.contains(fish.fish.name))return
        player.addAllowedCaughtFish(fish)

        broadCastPlayers("§f${player.name}§aが§e${players[player.uuid]?.allowedCaughtFish?.size}匹目§aの${targetFishName}を釣り上げた!")

        if(player.allowedCaughtFish.size >= targetFishAmount)end()
    }

    override fun onStart() {
        targetFishList=config.getStringList("targetFishes")
        targetFishName= config.getString("targetFishName","魚")!!
        targetFishAmount=config.getInt("amount",1)
        winningPlayerLimit=config.getInt("winnerPlayerLimit", 3)
        rewardCommands=config.getStringList("rewardCommands")

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
        rewardCommands?.forEach {
            dispatchCommand(it
                    .replace("<player>",winner!!.name)
                    .replace("<uuid>",winner!!.uniqueId.toString())
                    .replace("<world>",winner!!.world.name))
        }
    }
}