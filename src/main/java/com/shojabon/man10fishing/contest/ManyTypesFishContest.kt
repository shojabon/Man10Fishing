package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ManyTypesFishContest:AbstractFishContest() {


    //configからとるもの
    private var rewardCommands = HashMap<Int,List<String>>()


    private var winner: Player?=null

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(player.allowedCaughtFish.find { it.fish.alias==fish.fish.alias }!=null)return

        player.addAllowedCaughtFish(fish)

        updateRanking(player)

    }

    override fun onStart() {
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toInt()] = config.getStringList("rewardCommands.$it")
        }

        bossBar.setTitle("§e§lたくさんの種類の魚を釣れ！")

        time.setRemainingTime(config.getInt("time", 60))
    }

    override fun onEnd() {

        broadCastPlayers("§c§lコンテスト終了!!")

        //サブスレッドで実行されてると思ってる
        Thread.sleep(4000)

        if(winner==null){
            broadCastPlayers("§c§l魚を釣ったプレイヤーはいませんでした")
            return
        }


        for(i in 1..ranking.size){
            broadCastPlayers("§a${i}位: §e${ranking[i]?.name}§7:§b${ranking[i]?.allowedCaughtFish?.size}種類")
            val player= Bukkit.getPlayer(ranking[i]!!.uuid)?:continue
            rewardCommands[i]?.forEach {
                dispatchCommand(it.replace("&", "§")
                        .replace("<name>", player.name)
                        .replace("<uuid>", player.uniqueId.toString())
                        .replace("<count>", ranking[i]!!.allowedCaughtFish.size.toString())
                        .replace("<world>", player.world.name)
                        .replace("<and>", "&"))
            }
        }

    }


    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        return lowerPlayer.allowedCaughtFish.size<=higherPlayer.allowedCaughtFish.size
    }

}