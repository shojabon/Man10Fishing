package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.STimer
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
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

    //コンテスト表示用のボスバー
    val bossBar=Bukkit.createBossBar("§e§l魚を釣れ！", BarColor.BLUE, BarStyle.SOLID)

    //ランキング用のマップ
    val ranking= HashMap<Int,FishContestPlayer>()
    //ランキングのサイズ
    var rankingSize=10

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

    //データの変動があったプレイヤーを指定し、ランキングを更新する
    //ランキングシステムを使う場合はonCatchFishでこれを呼び出せばOK
    protected fun updateRanking(player:FishContestPlayer){

        //ランキングに上限人数未満のプレイヤーしか登録されていない場合
        if(ranking.size<rankingSize&&!ranking.containsValue(player)){
            ranking[ranking.size + 1] = player
            broadCastPlayers("§f${player.name}§aが§e${ranking.size}位§aにランクイン!")
            return

        }

        //ランキング更新用の変数
        var beforeRank=rankingSize

        for (i in ranking.size-1 downTo 1){
            //ランキング下位から順に比較を行い、自分以上の評価を持つ順位になった場合ifの中を通す
            if(rankingDefinition(player,ranking[i]!!)){

                //更新前にランクインをしていた場合必ずここを通り、取得する
                //更新前のランクが最下位位だった場合はbeforeRankの変数宣言で対応
                if(ranking[i]==player){
                    beforeRank=i
                    continue
                }

                //ここを通る際、i+1==beforeRankだった場合はランキングの変動がないことを意味するのでreturn
                if(i+1==beforeRank)return

                //ここを通るのはiがplayerの一つ上の順位になったときなので、それの１つ下にplayerを配置する
                //構造上beforeRank>=i+1は保証されている
                for(j in beforeRank downTo i+1) {
                    if(j==i+1)break
                    ranking[j] = ranking[j - 1]!!
                }
                ranking[i+1]=player

                broadCastPlayers("§e§l[§fランキング更新§e§l]§f${player.name}§aが§e${ranking.size}位§aにランクイン!")

                return
            }
        }
    }

    //順位の定義
    //lowerPlayer<=higherPlayerであるときにtrueを返すようにする
    open fun rankingDefinition(lowerPlayer:FishContestPlayer,higherPlayer:FishContestPlayer):Boolean{
        return false
    }


    //コンテストを開始する
    fun start(){
        Man10Fishing.nowContest = this
        onStart()
        if (time.originalTime != 0){
            time.start()
        }
        players.keys.forEach {
            bossBar.addPlayer(Bukkit.getPlayer(it)?:return@forEach)
        }
        time.linkBossBar(bossBar, true)
    }

    //コンテストを終了する 終わるときにはこの関数を使う
    fun end(){
        time.stop()
        Man10Fishing.nowContest = null
        onEnd()
        bossBar.removeAll()
    }

    //プレイヤー全員にメッセージを送信する
    fun broadCastPlayers(msg: String){
        players.forEach { p ->
            Bukkit.getPlayer(p.key)?.sendMessage(Man10Fishing.prefix + msg)
        }
    }

    //コマンドを発行する
    //プレイスホルダーは各自のコンテストでやりましょう
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