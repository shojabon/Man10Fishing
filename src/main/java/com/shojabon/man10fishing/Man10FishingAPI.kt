package com.shojabon.man10fishing

import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.man10fishing.scheduler.FishingScheduler
import com.shojabon.mcutils.Utils.SConfigFile
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

class Man10FishingAPI(private val plugin: Man10Fishing) {

    companion object {
        val rarity = HashMap<String, FishRarity>()
        val fish = HashMap<String, Fish>()
        val broadcastRarity=ArrayList<FishRarity>()
    }

    init {
        loadRarity()
        loadFish()
    }

    //　=========  コンフィグロード系統　=========

    //レアリティロード
    private fun loadRarity(){
        val configSection = plugin.config.getConfigurationSection("rarity") ?: return
        broadcastRarity.clear()
        for(rarityName in configSection.getKeys(false)){
            val alias = configSection.getString("$rarityName.alias")
            val weight = configSection.getInt("$rarityName.weight")
            val material = Material.getMaterial(configSection.getString("$rarityName.material")?:"STONE")?:Material.STONE
            val namePrefix=configSection.getString("$rarityName.namePrefix")?:""
            val loreDisplayName=configSection.getString("$rarityName.loreDisplayName")?:"未設定"
            val enabledItemIndex = configSection.getBoolean("$rarityName.enabledItemIndex", true)
            val minSellPrice = configSection.getDouble("$rarityName.minSellPrice", 0.0)
            val priceMultiplier = configSection.getDouble("$rarityName.priceMultiplier", 0.0)
            if(alias == null || weight == 0){
                Bukkit.getLogger().info("レアリティ$rarityName でエラーが発生しました")
                continue
            }
            val rarityObject = FishRarity(rarityName, alias, weight,material,namePrefix,
                loreDisplayName, enabledItemIndex, minSellPrice, priceMultiplier)
            if(configSection.getBoolean("broadcast")) broadcastRarity.add(rarityObject)
            rarity[rarityName] = rarityObject
        }
    }

    //魚ロード
    private fun loadFish(){
        for(file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.toString() + File.separator + "fish")){
            val config = SConfigFile.getConfigFile(file.path)?: continue
            for(fish in config.getKeys(false)){
                val singleFish = config.getConfigurationSection(fish)?: continue
                val fishObject = Fish(fish, singleFish)
                if(!fishObject.loaded) continue
                Man10FishingAPI.fish[fish] = fishObject
                val rarity = rarity[fishObject.rarity]?: continue
                rarity.fishInGroup.add(fishObject)
            }

        }
    }

    //　======================================

    //魚取得
    fun getFish(name: String): Fish?{
        return fish[name]
    }

    //レアリティ選択
    fun pickRarity(): FishRarity? {
        var total = 0
        for(rarity in rarity.values){
            if(rarity.fishInGroup.size == 0) continue
            total += rarity.weight
        }
        if(total == 0) return null

        val rand = Random.nextInt(0, total)
        var checkingTotal = 0
        for(rarity in rarity.values){
            if(rarity.fishInGroup.size == 0) continue
            checkingTotal += rarity.weight
            if(rand <= checkingTotal-1) return rarity
        }
        return null
    }

    //魚選択
    fun pickFish(fisher: Player,rod: FishingRod,hookLocation: Location): Fish? {
        val rarity = pickRarity()?: return null
        val fishGroup = createFishTable(rarity.fishInGroup, fisher,rod,hookLocation)

        var total = 0
        for(weight in fishGroup.values){
            total += (weight * 100).toInt()
        }
        if(total == 0) return null

        val rand = Random.nextInt(0, total)
        var checkingTotal = 0
        for(fishName in fishGroup.keys){
            val fishObject = fish[fishName]?: continue
            val multiplier = fishGroup[fishName]?: continue
            checkingTotal += (multiplier * 100).toInt()

            if(rand <= checkingTotal-1) return fishObject
        }

        return null
    }

    //抽選のための魚テーブル作成 (内部名, 出現確立)
    fun createFishTable(fishAvailableToFish: ArrayList<Fish>, fisher: Player,rod: FishingRod,hookLocation: Location): HashMap<String, Float>{
        val result = HashMap<String, Float>()
        for(fish in fishAvailableToFish){
            if(!fish.isFishEnabled(fisher, rod,hookLocation)) continue
            result[fish.name] = fish.getRarityMultiplier(fisher, rod)
        }
        return result
    }

    //contest一覧取得
    fun getContestList(): ArrayList<String>{
        val result = ArrayList<String>()
        for(file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.path + File.separator + "contests")){
            SConfigFile.getConfigFile(file.path)?:continue
            result.add(file.nameWithoutExtension)
        }
        return result
    }

    fun loadSchedulers(){
        Man10Fishing.schedulerManager.schedulers.clear()
        for (file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.path + File.separator + "schedulers")){
            Man10Fishing.schedulerManager.schedulers.add(FishingScheduler.newInstance(file.nameWithoutExtension)?:continue)
        }
    }

    fun loadItemIndexes(){
        ItemIndex.itemIndexes.clear()
        for (file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.path + File.separator + "itemIndexes")){
            ItemIndex.itemIndexes[file.nameWithoutExtension] = ItemIndex.fromConfig(file)
        }

        for (rarity in rarity.values){
            if(!rarity.enabledItemIndex) continue
            ItemIndex.itemIndexes[rarity.name] = ItemIndex.fromRarity(rarity)
        }
    }

    fun getFishingAmount(uuid: UUID, fish: String? = null, rarity: String? = null): Long {
        val query = StringBuilder("SELECT COUNT(*) FROM fish_log WHERE uuid = '${uuid}'")
        if(fish != null) query.append(" AND fish = '${fish}'")
        if(rarity != null) query.append(" AND rarity = '${rarity}'")
        val future = Man10Fishing.mysql.futureQuery(query.toString())
        return future.get()[0].getLong("COUNT(*)")
    }

    //DB作成
    fun createTables(){
        Man10Fishing.mysql.asyncExecute("CREATE TABLE IF NOT EXISTS `fish_log` (\n" +
                "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                "\t`fish` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`rarity` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`date_time` TEXT NOT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`name` VARCHAR(16) NOT NULL DEFAULT '' COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`uuid` VARCHAR(36) NOT NULL DEFAULT '' COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`size` DOUBLE NOT NULL DEFAULT '0',\n" +
                "\tPRIMARY KEY (`id`) USING BTREE\n" +
                ")\n" +
                "COLLATE='utf8mb4_0900_ai_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";\n") {}
    }

    //prefix付きの全体メッセージを流す
    fun broadcastPlMessage(message:String){
        Bukkit.getServer().broadcast(Component.text(Man10Fishing.prefix+message))
    }

    //現在の季節を返す
    //基準は1990/1/4の日曜日
    //季節は、基準日から7日を春として1週間ごとに春→夏→秋→冬→春...と帰納的に定義される
    fun getCurrentSeason(): Season {
        return when((((Date().time/864000000L)-3L)/7)%4){
            0L->Season.SPRING
            1L->Season.SUMMER
            2L->Season.AUTUMN
            else->Season.WINTER
            //処理の関係上elseとなっているが、これは3Lの場合のみしか通らない
        }
    }


    //季節に応じたスポーン場所を返す
    fun getSpawnLocation():Location?{
        return Man10Fishing.spawnPoints[getCurrentSeason()]
    }

}