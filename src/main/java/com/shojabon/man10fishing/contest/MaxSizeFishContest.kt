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

    private val bossBar = Bukkit.createBossBar("§e§l最も大きい魚を釣れ！", BarColor.BLUE, BarStyle.SOLID)

    override fun onStart() {
        time.setRemainingTime(config.getInt("time", 60))
        winnerPlayerLimit = config.getInt("winnerPlayerLimit", 3)
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toInt()] = config.getStringList("rewardCommands.$it")
        }

        players.keys.forEach {
            bossBar.addPlayer(Bukkit.getPlayer(it)?:return@forEach)
        }
        time.linkBossBar(bossBar, true)
    }

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {
        Bukkit.getPlayer(player.uuid)?.sendMessage(Man10Fishing.prefix + "${fish.fish.alias}§aを釣り上げた！§d(${fish.size}cm)")

        val find = maxSizePlayers.find { it.first.uuid == player.uuid }
        if (find != null){
            if (find.second.size <= fish.size){
                maxSizePlayers.remove(find)
            } else return
        }

        val filter = maxSizePlayers.filter { it.second.size < fish.size }

        if (filter.isNotEmpty()){

            if (maxSizePlayers.size >= winnerPlayerLimit){
                val min = filter.minByOrNull { it.second.size }!!
                maxSizePlayers.remove(min)
            }
            maxSizePlayers.add(Pair(player, fish))
            broadCastPlayers("§e§l${player.name}が§b${fish.fish.alias}§e§lを釣り上げた！§d§l(${fish.size}cm)")
        } else {
            if (maxSizePlayers.size >= winnerPlayerLimit)return
            maxSizePlayers.add(Pair(player, fish))
            broadCastPlayers("§e§l${player.name}が§b${fish.fish.alias}§e§lを釣り上げた！§d§l(${fish.size}cm)")
        }

        if (maxSizePlayers.maxOf { it.second.size } == fish.size){
            bossBar.setTitle("§e§l最も大きい魚を釣れ！§b${fish.fish.alias}§d(${fish.size}cm)")
        }
    }

    override fun onEnd() {
        bossBar.removeAll()
        if (maxSizePlayers.isEmpty()) {
            broadCastPlayers("§c§l魚が一匹も釣られませんでした")
            return
        }

        broadCastPlayers("§c§l順位")
        Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
            maxSizePlayers.sortedBy { it.second.size }.forEachIndexed { index, pair ->
                broadCastPlayers("§a${index + 1}位: §e${pair.first.name}§7:§b${pair.second.fish.alias}§d(${pair.second.size}cm)")
                val commands = rewardCommands[index + 1]?:return@forEachIndexed
                val player = Bukkit.getPlayer(pair.first.uuid)?:return@forEachIndexed
                commands.forEach {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it
                        .replace("&", "§")
                        .replace("<name>", pair.first.name)
                        .replace("<uuid>", pair.first.uuid.toString())
                        .replace("<fish>", pair.second.fish.alias)
                        .replace("<size>", pair.second.size.toString())
                        .replace("<world>", player.world.name)
                        .replace("<and>", "&"))
                }
            }
        })
    }
}