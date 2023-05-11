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


        ranking.forEach { (t, u) ->
            broadCastPlayers("§a${t}位: §e${u.name}§7:§b${u.allowedCaughtFish.size}匹")
            val player = Bukkit.getPlayer(u.uuid)?:return@forEach
            if(!rewardCommands.containsKey(t))return@forEach
            rewardCommands[t]?.forEach {
                dispatchCommand(it.replace("&", "§")
                        .replace("<name>", player.name)
                        .replace("<uuid>", player.uniqueId.toString())
                        .replace("<count>", u.allowedCaughtFish.size.toString())
                        .replace("<world>", player.world.name)
                        .replace("<and>", "&"))
            }
        }

    }


    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        return lowerPlayer.allowedCaughtFish.size<=higherPlayer.allowedCaughtFish.size
    }

}