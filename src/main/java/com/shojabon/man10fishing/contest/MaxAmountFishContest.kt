package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

class MaxAmountFishContest:AbstractFishContest() {


    private val targetFishList=config.getList("targetFishes")
    private val targetFishName=config.getString("targetFishName","魚")

    private var winningPlayerLimit=config.getInt("winnerPlayerLimit", 3)
    private var rewardCommands = HashMap<Int,List<String>>()

    private val bossBar = Bukkit.createBossBar("§e§l最も多く&c&l${targetFishName}&e&lを釣れ！", BarColor.BLUE, BarStyle.SOLID)

    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList!=null&&!targetFishList.contains(fish.fish.name))return





    }

    override fun onStart() {

        time.setRemainingTime(config.getInt("time", 60))
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it.toInt()] = config.getStringList("rewardCommands.$it")
        }

        players.keys.forEach {
            bossBar.addPlayer(Bukkit.getPlayer(it)?:return@forEach)
        }
        time.linkBossBar(bossBar, true)
    }

    override fun onEnd() {

    }





}