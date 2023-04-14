package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

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

        player.addFishCount()





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

    }

    //ランキングを更新する
    private fun updateRanking(player:FishContestPlayer){


        //ランキングに上限人数未満のプレイヤーしか登録されていない場合
        if(ranking.size<winningPlayerLimit){
            ranking[ranking.size+1]=player
            broadCastPlayers("§f${player.name}§aが${ranking.size}位にランクイン!")
            return
        }

        //ランキング更新用の変数
        var beforeRank=winningPlayerLimit

        for (i in winningPlayerLimit downTo 1){
            if((ranking[i]?.allowedFishCount?:0)>=player.allowedFishCount){

                //更新前のランキングがあればそれを取得する
                if(ranking[i]==player){
                    beforeRank=i
                    continue
                }

                //ここを通るのはiがplayerの一つ上の順位になったときなので、それの１つ下にplayerを配置する
                //構造上beforeRank>=i+1は保証されている
                for(j in beforeRank downTo i+1) {
                    if(j==i+1)break
                    ranking[j] = ranking[j - 1]!!
                }
                ranking[i+1]=player
                return
            }
        }
    }

}