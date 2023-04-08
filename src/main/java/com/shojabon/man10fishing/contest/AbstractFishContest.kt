package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.STimer
import org.bukkit.Bukkit
import java.util.UUID

abstract class AbstractFishContest {

    val players = HashMap<UUID, FishContestPlayer>()
    var time = STimer().apply { addOnEndEvent { end() } }

    abstract fun onStart()

    open fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {}

    abstract fun onEnd()



    fun start(){
        onStart()
        if (time.originalTime != 0){
            time.start()
        }
    }
    fun end(){
        time.stop()
        onEnd()
    }
    fun broadCastPlayers(msg: String){
        players.forEach { p ->
            Bukkit.getPlayer(p.key)?.sendMessage(msg)
        }
    }
}