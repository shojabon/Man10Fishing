package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

/*

該当の魚を釣った数を競うコンテスト
数が同じである場合、先にその数を達成した方を上位とする

*/
class MaxAmountFishContest:AbstractFishContest() {


    //configからとるもの
    private lateinit var targetFishList:List<String>
    private var targetFishName="魚"
    private var winningPlayerLimit=10
    private var rewardCommands = HashMap<Int,List<String>>()



    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty() &&!targetFishList.contains(fish.fish.name))return

        player.addAllowedCaughtFish(fish)

        broadCastPlayers("${player.name}§aが§e${fish.fish.alias}§aを釣り上げた!")
    }

    override fun onStart() {

        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!
        winningPlayerLimit=config.getInt("winnerPlayerLimit", 10)
        rankingSize=config.getInt("rankingSize",winningPlayerLimit)

        bossBar.setTitle("§e§l最も多く§c§l${targetFishName}§e§lを釣れ！")

        time.setRemainingTime(config.getInt("time", 60))
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toInt()] = config.getStringList("rewardCommands.$it")
        }
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

    override fun rankingDefinition(lowerPlayer:FishContestPlayer,higherPlayer:FishContestPlayer):Boolean{
        return lowerPlayer.allowedCaughtFish.size<=higherPlayer.allowedCaughtFish.size
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.size}匹"
    }

}