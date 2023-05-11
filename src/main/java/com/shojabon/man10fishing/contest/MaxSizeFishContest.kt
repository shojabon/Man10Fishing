package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

class MaxSizeFishContest: AbstractFishContest() {

    private var maxSizePlayers = ArrayList<Pair<FishContestPlayer, FishParameter>>()
    private var winnerPlayerLimit = 3
    private var rewardCommands = HashMap<Int,List<String>>()


    override fun onStart() {
        time.setRemainingTime(config.getInt("time", 60))
        winnerPlayerLimit = config.getInt("winnerPlayerLimit", 3)
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toIntOrNull()?:return@forEach] = config.getStringList("rewardCommands.$it")
        }

        bossBar.setTitle("§e§l最も大きい魚を釣れ！")
    }

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {
        Bukkit.getPlayer(player.uuid)?.sendMessage(Man10Fishing.prefix + "${fish.fish.alias}§aを釣り上げた！§d(${fish.size}cm)")

        if (player.allowedCaughtFish.isNotEmpty() && player.allowedCaughtFish[0].size < fish.size){
            player.allowedCaughtFish.removeFirstOrNull()
        }

        player.addAllowedCaughtFish(fish)

        val max = players.values.mapNotNull { it.allowedCaughtFish.firstOrNull()?.size }.maxOrNull()
        if (max != null && max == fish.size){
            bossBar.setTitle("§e§l最も大きい魚を釣れ！§b${fish.fish.alias}§d(${fish.size}cm)")
        }

        updateRanking(player)
    }

    override fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer): Boolean {
        if (lowerPlayer.allowedCaughtFish.isEmpty()){
            return higherPlayer.allowedCaughtFish.isNotEmpty()
        }

        if (higherPlayer.allowedCaughtFish.isEmpty()){
            return false
        }

        return lowerPlayer.allowedCaughtFish.first().size > higherPlayer.allowedCaughtFish.first().size
    }

    override fun onEnd() {
        if (maxSizePlayers.isEmpty()) {
            broadCastPlayers("§c§l魚が一匹も釣られませんでした")
            return
        }

        broadCastPlayers("§c§lコンテスト終了!!")
        broadCastPlayers("§c§l順位")
        maxSizePlayers.sortedBy { it.second.size }.forEachIndexed { index, pair ->
            broadCastPlayers("§a${index + 1}位: §e${pair.first.name}§7:§b${pair.second.fish.alias}§d(${pair.second.size}cm)")
            val commands = rewardCommands[index + 1]?:return@forEachIndexed
            val player = Bukkit.getPlayer(pair.first.uuid)?:return@forEachIndexed
            commands.forEach {
                dispatchCommand(it
                    .replace("&", "§")
                    .replace("<name>", pair.first.name)
                    .replace("<uuid>", pair.first.uuid.toString())
                    .replace("<fish>", pair.second.fish.alias)
                    .replace("<size>", pair.second.size.toString())
                    .replace("<world>", player.world.name)
                    .replace("<and>", "&"))
            }
        }
    }
}