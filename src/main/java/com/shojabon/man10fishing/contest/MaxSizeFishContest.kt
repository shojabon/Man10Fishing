package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

class MaxSizeFishContest: AbstractFishContest() {

    private var rewardCommands = HashMap<Int,List<String>>()


    override fun onStart() {
        time.setRemainingTime(config.getInt("time", 60))
        rankingSize = config.getInt("winnerPlayerLimit", 3)
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toIntOrNull()?:return@forEach] = config.getStringList("rewardCommands.$it")
        }

        bossBar.setTitle("§e§l最も大きい魚を釣れ！")
    }

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {
        Bukkit.getPlayer(player.uuid)?.sendMessage(Man10Fishing.prefix + "${fish.fish.alias}§aを釣り上げた！§d(${fish.size}cm)")

        if(player.allowedCaughtFish.isEmpty()){
            player.addAllowedCaughtFish(fish)
        }
        else if (player.allowedCaughtFish[0].size < fish.size){
            player.allowedCaughtFish.removeFirstOrNull()
            player.addAllowedCaughtFish(fish)
        }

        val max = players.values.mapNotNull { it.allowedCaughtFish.firstOrNull()?.size }.maxOrNull()
        if (max != null && max == fish.size){
            bossBar.setTitle("§e§l最も大きい魚を釣れ！§b${fish.fish.alias}§d(${fish.size}cm)")
        }

    }

    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        if (lowerPlayer.allowedCaughtFish.isEmpty()){
            return higherPlayer.allowedCaughtFish.isNotEmpty()
        }

        if (higherPlayer.allowedCaughtFish.isEmpty()){
            return false
        }

        return lowerPlayer.allowedCaughtFish.first().size <= higherPlayer.allowedCaughtFish.first().size
    }

    override fun rankingLowerPrefix(player: FishContestPlayer): String {
        return "${player.allowedCaughtFish.first().size}cm"
    }

    override fun onEnd() {
        if (ranking.isEmpty()) {
            broadCastPlayers("§c§l魚が一匹も釣られませんでした")
            return
        }

        broadCastPlayers("§c§lコンテスト終了!!")

        Thread.sleep(4000)

        broadCastPlayers("§c§l順位")

        Thread.sleep(500)

        ranking.forEach { (i, data) ->
            val fish = data.allowedCaughtFish.firstOrNull()?:return@forEach
            broadCastPlayers("§a${i}位: §e${data.name}§7:" +
                    "§b${fish.name}" +
                    "§d(${fish.size}cm)")
            val commands = rewardCommands[i]?:return@forEach
            val player = Bukkit.getPlayer(data.uuid)?:return@forEach
            commands.forEach {
                dispatchCommand(it
                    .replace("&", "§")
                    .replace("<name>", data.name)
                    .replace("<uuid>", data.uuid.toString())
                    .replace("<fish>", fish.name)
                    .replace("<size>", fish.size.toString())
                    .replace("<world>", player.world.name)
                    .replace("<and>", "&"))
            }
        }
    }
}