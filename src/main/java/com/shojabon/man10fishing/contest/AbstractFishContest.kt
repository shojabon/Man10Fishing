package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.STimer
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

abstract class AbstractFishContest {

    val players = HashMap<UUID, FishContestPlayer>()
    var time = STimer().apply { addOnEndEvent { end() } }
    protected lateinit var config: YamlConfiguration

    fun setConfig(config: YamlConfiguration): AbstractFishContest {
        this.config = config
        return this
    }

    abstract fun onStart()

    open fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {}

    abstract fun onEnd()


    fun start(){
        Man10Fishing.nowContest = this
        onStart()
        if (time.originalTime != 0){
            time.start()
        }
    }

    fun end(){
        time.stop()
        onEnd()
        Man10Fishing.nowContest = null
    }

    fun broadCastPlayers(msg: String){
        players.forEach { p ->
            Bukkit.getPlayer(p.key)?.sendMessage(Man10Fishing.prefix + msg)
        }
    }

    fun dispatchCommand(command:String){
        Bukkit.getScheduler().runTask(Man10Fishing.instance,Runnable{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command)
        })
    }

    companion object{
        fun newInstance(name: String): AbstractFishContest? {
            return try {
                val config = YamlConfiguration.loadConfiguration(File("${Man10Fishing.instance.dataFolder.path}/contests/${name}.yml"))
                val clazz = Class.forName("com.shojabon.man10fishing.contest.${config.getString("game")}")
                val instance = clazz.getConstructor().newInstance() as AbstractFishContest
                instance.setConfig(config)
            } catch (e: Exception) {
                null
            }

        }
    }
}