package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.contest.data.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.STimer
import com.sk89q.worldedit.bukkit.BukkitAdapter
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.DisplaySlot
import java.io.File
import java.util.UUID
import java.util.logging.Level
import kotlin.math.min

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

    private val rewardCommands=HashMap<String,List<String>>()
    private var updating=false
    lateinit var name:String
    private val areas=ArrayList<String>()

    lateinit var targetFishList:List<String>
    var targetFishName="魚"


    //コンテスト表示用のボスバー
    val bossBar=Bukkit.createBossBar("§e§l魚を釣れ！", BarColor.BLUE, BarStyle.SOLID)

    //ランキング用
    val ranking= HashMap<Int, FishContestPlayer>()
    private var useRanking=false
    var visibleRankingSize=7
    val hideRanking= mutableListOf<UUID>()

    fun setConfig(config: YamlConfiguration,name:String): AbstractFishContest {
        this.config = config
        this.name=name
        visibleRankingSize=config.getInt("rankingSize",7)
        targetFishList=config.getStringList("targetFishes")
        targetFishName=config.getString("targetFishName","魚")!!

        areas.addAll(config.getString("area")?.split(",")?:listOf("none"))
        return this
    }

    //始まったときの処理
    protected abstract fun onStart()

    //釣れたときの処理
    protected open fun onCaughtFish(player: FishContestPlayer, fish: FishParameter) {}

    //終わったときの処理
    protected abstract fun onEnd()


    //順位の入れ替え
    //表示ランキング内の入れ替えが発生したら通知関数を通す
    private fun updateRanking(player:FishContestPlayer){

        var newParticipant=false
        //現在の順位取得
        val oldRank=ranking.filter{ it.value==player}.keys.firstOrNull()?: kotlin.run {
            ranking[ranking.size+1]=player
            newParticipant=true
            ranking.size
        }

        var newRank=oldRank

        for(i in oldRank-1 downTo 1){
            //下から順番に評価を行い, 初めて自分よりも上のランクが現れたときにランキング更新を適用する.
            if(rankingDefinition(player,ranking[i]!!)){
                for(j in oldRank downTo  newRank+1){
                    ranking[j]=ranking[j-1]!!
                }
                ranking[newRank]=player
                break
            }
            newRank=i
        }
        if(newRank==1){
            for(i in oldRank downTo  2){
                ranking[i]=ranking[i-1]!!
            }
            ranking[1]=player
        }

        if(useRanking&&(newRank<oldRank||newParticipant)&&newRank<=visibleRankingSize){
            broadCastPlayers(rankingUpdateMessage(player,newRank))
        }

    }

    protected open fun rankingUpdateMessage(player: FishContestPlayer,num:Int):String{
        return "§fランキング更新§e§l：§c${player.name}§aが§e${num}位§aにランクイン!"
    }

    private fun executeRewardCommands(){
        rewardCommands.forEach { (t, u) ->
            if(t!="all"){
                val num=t.toInt()
                ranking[num]?.let { player->
                    u.forEach{
                        dispatchCommand(applyAdditionalPlaceHolder(applyPlaceHolder(it,player),player))
                    }
                }
            }
            if(t=="all"){
                val ipList= arrayListOf<String>()
                rewardCommands[t]?.forEach { str ->
                    players.values.forEach { cPlayer ->
                        if(cPlayer.caughtFish.isEmpty())return@forEach
                        Man10Fishing.instance.logger.info("コンテスト${t}位:${cPlayer.name}")
                        val player=Bukkit.getPlayer(cPlayer.uuid)?:return@forEach
                        val ip=player.address?.address?.hostAddress?:return@forEach
                        if(ipList.contains(ip)){
                            player.sendMessage("${Man10Fishing.prefix}§c複数のアカウントで参加賞を受け取ることはできません。")
                            return@forEach
                        }
                        ipList.add(ip)
                        dispatchCommand(applyAdditionalPlaceHolder(applyPlaceHolder(str, cPlayer), cPlayer))
                    }
                }
            }
        }
    }

    //全てのイベントで共通のplaceholder
    private fun applyPlaceHolder(str:String,contestPlayer: FishContestPlayer):String{
        return str
                .replace("<player>",contestPlayer.name)
                .replace("<uuid>", contestPlayer.uuid.toString())
                .replace("<count>", contestPlayer.allowedCaughtFish.size.toString())
                .replace("&&","<and>")
                .replace("&","§")
                .replace("<and>", "&")
    }

    //
    protected open fun applyAdditionalPlaceHolder(str:String,contestPayer: FishContestPlayer):String{
        return str
    }

    //順位の定義
    //lowerPlayer<=higherPlayerであるときにtrueを返すようにする
    protected open fun rankingDefinition(lowerPlayer: FishContestPlayer, higherPlayer: FishContestPlayer):Boolean{
        return false
    }

    //ランキング表示における追加情報
    protected open fun rankingLowerPrefix(player: FishContestPlayer):String{
        return ""
    }

    fun displayScoreboardRanking(){
        if(!useRanking)return
        val rankingScoreBoard=Bukkit.getScoreboardManager().newScoreboard
        val rankingObjective=rankingScoreBoard.registerNewObjective("fish_con","Dummy", Component.text("§e§l釣り大会ランキング"))
        rankingObjective.displaySlot=DisplaySlot.SIDEBAR
        for(i in 1..visibleRankingSize){
            if(ranking.containsKey(i)){
                rankingObjective.getScore("§6${i}§f:§e${ranking[i]!!.name}§f,§b${rankingLowerPrefix(ranking[i]!!)}§r").score=visibleRankingSize-i
            }
            else{
                rankingObjective.getScore("§6${i}§f:§c無し").score=visibleRankingSize-i
            }
        }
        for(player in Bukkit.getServer().onlinePlayers){
            if(hideRanking.contains(player.uniqueId))continue
            player.scoreboard=rankingScoreBoard
        }
    }


    //コンテストを開始する
    fun start(){
        Man10Fishing.nowContest = this
        Man10Fishing.api.broadcastPlMessage("§a§lコンテストが開始されました!")

        getContestInfoMessage().forEach{
            Man10Fishing.instance.server.broadcast(Component.text(it))
        }

        Man10Fishing.instance.logger.info("コンテスト開始:${name}")
        onStart()
        if (time.originalTime != 0){
            time.start()
        }

        useRanking=config.getBoolean("useRanking",true)
        if(useRanking){
            displayScoreboardRanking()
        }

        players.keys.forEach {
            val player=Bukkit.getPlayer(it)?:return@forEach
            bossBar.addPlayer(player)
        }

        //rewardコマンド読み込み
        config.getConfigurationSection("rewardCommands")?.getKeys(false)?.forEach {
            rewardCommands[it] = config.getStringList("rewardCommands.$it")
        }
        time.linkBossBar(bossBar, true)
    }

    //コンテストを終了する 終わるときにはこの関数を使う
    fun end(){
        time.stop()
        Man10Fishing.nowContest = null
        Thread{
            onEnd()
            while (updating){
                Thread.sleep(1)
            }
            executeRewardCommands()
        }.start()
        bossBar.removeAll()
        if(useRanking){
            Bukkit.getScheduler().runTask(Man10Fishing.instance, Runnable {
                val scoreboard=Bukkit.getScoreboardManager().newScoreboard
                for(player in Bukkit.getOnlinePlayers()){
                    if(hideRanking.contains(player.uniqueId))continue
                    player.scoreboard=scoreboard
                }
            })

        }
        ranking[1]?.let {data->
            Bukkit.getPlayer(data.uuid)?.let {
                Man10Fishing.instance.server.scheduler.runTask(Man10Fishing.instance, Runnable {
                    val fw=it.world.spawnEntity(it.location, EntityType.FIREWORK) as Firework
                    val meta=fw.fireworkMeta
                    meta.power=1
                    val eff= FireworkEffect.builder().withColor(Color.LIME).withColor(Color.WHITE).withColor(Color.YELLOW)
                            .with(FireworkEffect.Type.BALL_LARGE).flicker(true).build()
                    meta.addEffect(eff)
                    fw.fireworkMeta=meta
                })
            }
        }
    }

    fun caughtFish(player:Player,fish: FishParameter,location: Location){
        if (!players.containsKey(player.uniqueId))return
        val contestPlayer =players[player.uniqueId]!!
        contestPlayer.caughtFish.add(fish)

        //ここにエリア判定処理入れるのあんま良くないかも
        if(!areas.contains("none")) {
            var outOfArea=true
            for (region in Man10Fishing.regionContainer!![BukkitAdapter.adapt(location.world)]!!.regions) {
                for (area in areas) {
                    if (region.key.startsWith(area)) {
                        if (region.value.contains(BukkitAdapter.asBlockVector(location))) {

                            broadCastPlayers(region.key)
                            outOfArea = false
                        }
                    }
                }
            }
            if(outOfArea)return
        }

        updating=true
        onCaughtFish(contestPlayer,fish)

        //コンテスト対象の魚を釣っていた場合のみランキング更新処理を行う
        if(contestPlayer.allowedCaughtFish.isNotEmpty()) {
            updateRanking(contestPlayer)
            if (useRanking) {
                displayScoreboardRanking()
            }
        }
        updating=false
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

    fun onJoin(e:PlayerJoinEvent){
        val player=e.player
        bossBar.addPlayer(player)
        if (!players.containsKey(player.uniqueId)) {
            players[player.uniqueId] = FishContestPlayer(player.uniqueId, player.name)
        }
        if(useRanking){
            displayScoreboardRanking()
        }
    }

    fun getContestInfoMessage():ArrayList<String>{
        val message= arrayListOf("${Man10Fishing.prefix}§e========================","${Man10Fishing.prefix}§c対象となる魚")
        targetFishList.forEach { str->
            Man10FishingAPI.fish[str]?.let { fish->
                message.add("${Man10Fishing.prefix}§r${fish.alias}")
            }
        }
        if(targetFishList.isEmpty()){
            message.add("${Man10Fishing.prefix}§r${targetFishName}")
        }
        message.add("${Man10Fishing.prefix}§e========================")
        return message
    }

    companion object{
        fun newInstance(name: String): AbstractFishContest? {
            return try {
                val config = YamlConfiguration.loadConfiguration(File("${Man10Fishing.instance.dataFolder.path}/contests/${name}.yml"))
                val clazz = Class.forName("com.shojabon.man10fishing.contest.${config.getString("game")}")
                val instance = clazz.getConstructor().newInstance() as AbstractFishContest
                instance.setConfig(config,name)
            } catch (e: Exception) {
                null
            }

        }
    }
}