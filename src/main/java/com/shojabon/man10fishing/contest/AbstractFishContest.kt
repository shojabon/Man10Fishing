package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.STimer
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID

/**
 * コンテストの基盤
 */
abstract class AbstractFishContest() {

    //コンテストに参加しているプレイヤー 今は鯖にいる全員が参加
    val players = HashMap<UUID, FishContestPlayer>()
    //タイマー
    var time = STimer().apply { addOnEndEvent { end() } }
    //コンフィグ
    protected lateinit var config: YamlConfiguration

    constructor(config: YamlConfiguration): this() {
        this.config = config
    }

    fun setConfig(config: YamlConfiguration): AbstractFishContest {
        this.config = config
        return this
    }

    //始まったときの処理
    abstract fun onStart()

    //釣れたときの処理
    open fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {}

    //終わったときの処理
    abstract fun onEnd()


    //コンテストを開始する
    fun start(){
        Man10Fishing.nowContest = this
        onStart()
        if (time.originalTime != 0){
            time.start()
        }
    }

    //コンテストを終了する 終わるときにはこの関数を使う
    fun end(){
        time.stop()
        onEnd()
        Man10Fishing.nowContest = null
    }

    //プレイヤー全員にメッセージを送信する
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