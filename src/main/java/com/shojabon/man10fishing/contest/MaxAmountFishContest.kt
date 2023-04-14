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
    private var targetFishList:List<String>?=null
    private var targetFishName="魚"
    private var winningPlayerLimit=10
    private var rewardCommands = HashMap<Int,List<String>>()


    private val ranking=HashMap<Int,FishContestPlayer>()
    private val bossBar = Bukkit.createBossBar("§e§l最も多く&c&l${targetFishName}&e&lを釣れ！", BarColor.BLUE, BarStyle.SOLID)


    override fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {

        if(targetFishList!=null&&!targetFishList!!.contains(fish.fish.name))return

        player.plusOneFishCount()

        broadCastPlayers("${player.name}§aが§e${fish.name}を釣り上げた!")

        updateRanking(player)
    }

    override fun onStart() {

        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!
        winningPlayerLimit=config.getInt("winnerPlayerLimit", 10)

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
        for(i in 1..ranking.size){
            broadCastPlayers("§a${i}位: §e${ranking[i]?.name}§7:§b${ranking[i]?.allowedFishCount}匹")
            val player = Bukkit.getPlayer(ranking[i]!!.uuid)?:continue
            rewardCommands[i]?.forEach {
                dispatchCommand(it.replace("&", "§")
                        .replace("<name>", player.name)
                        .replace("<uuid>", player.uniqueId.toString())
                        .replace("<count>", ranking[i]!!.allowedFishCount.toString())
                        .replace("<world>", player.world.name)
                        .replace("<and>", "&"))
            }
        }

    }

    //ランキングを更新する
    //可読性だいぶ低い、ゴメン!
    private fun updateRanking(player:FishContestPlayer){

        //ランキングに上限人数未満のプレイヤーしか登録されていない場合
        if(ranking.size<winningPlayerLimit){
            ranking[ranking.size+1]=player
            broadCastPlayers("§f${player.name}§aが${ranking.size}位にランクイン!")
            return
        }

        //ランキング更新用の変数
        var beforeRank=winningPlayerLimit

        for (i in winningPlayerLimit-1 downTo 1){
            if((ranking[i]?.allowedFishCount?:0)>=player.allowedFishCount){

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

                broadCastPlayers("§e§l[§fランキング更新§e§l]§f${player.name}§aが${ranking.size}位にランクイン!")

                return
            }
        }
    }

}