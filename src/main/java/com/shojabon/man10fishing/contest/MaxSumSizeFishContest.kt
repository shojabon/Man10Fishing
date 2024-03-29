package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class MaxSumSizeFishContest:AbstractFishContest() {


    //configからとるもの
    private lateinit var targetFishList:List<String>
    private var targetFishName="魚"
    private var rewardCommands = HashMap<Int,List<String>>()


    private var winner: Player?=null

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList.isNotEmpty()&&!targetFishList.contains(fish.fish.name))return

        player.addAllowedCaughtFish(fish)

        Bukkit.getPlayer(player.uuid)?.sendMessage("§c現在のサイズ合計：§e${player.allowedCaughtFish.sumOf { it.size }}cm")


    }

    override fun onStart() {

        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!
        rankingSize=config.getInt("rankingSize",10)

        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toInt()] = config.getStringList("rewardCommands.$it")
        }

        bossBar.setTitle("§e§l大きな§c§l${targetFishName}§e§lをたくさん釣れ！")

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
        return lowerPlayer.allowedCaughtFish.sumOf { it.size }<=higherPlayer.allowedCaughtFish.sumOf { it.size }
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.sumOf { it.size }}cm"
    }
}