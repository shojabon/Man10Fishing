package com.shojabon.man10fishing

import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.man10fishing.dataClass.FishRecordData
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.man10fishing.dataClass.treasure.Treasure
import com.shojabon.man10fishing.dataClass.treasure.TreasureTable
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.man10fishing.scheduler.FishingScheduler
import com.shojabon.mcutils.Utils.SConfigFile
import com.sk89q.worldedit.bukkit.BukkitAdapter
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
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

        val treasure=HashMap<String,Treasure>()
        val treasureTables=HashMap<String, TreasureTable>()



        //コンテストをseason/categoryごとに分けるためのlist
        //あとで実装方法変えるかも
        //category名-contest名のリスト
        val seasonContests=HashMap<Season,ArrayList<String>>()
        val categorizedContests=HashMap<String,ArrayList<String>>()

        val fishRecords=HashMap<String,FishRecordData>()
    }

    init {
        loadRarity()
        loadFish()
        loadTreasures()
        loadTreasureTables()
        loadContestName()
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
            val broadcast=configSection.getBoolean("$rarityName.broadcast")
            val firework=configSection.getBoolean("$rarityName.firework")
            val hidden=configSection.getBoolean("$rarityName.hidden",false)
            if(alias == null || weight == 0){
                Bukkit.getLogger().info("レアリティ$rarityName でエラーが発生しました")
                continue
            }
            val rarityObject = FishRarity(rarityName, alias, weight,material,namePrefix,
                loreDisplayName, enabledItemIndex, minSellPrice, priceMultiplier,broadcast,firework,hidden)
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

    private fun loadTreasures(){
        treasure.clear()
        for(file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.toString() + File.separator + "treasure")){
            val config = SConfigFile.getConfigFile(file.path)?: continue
            for(key in config.getKeys(false)){
                treasure[key]=Treasure(key,config.getConfigurationSection(key)!!)
            }
        }
    }

    private fun loadTreasureTables(){
        treasureTables.clear()

        val tmpTable=HashMap<String,TreasureTable>()

        for(file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.toString() + File.separator + "treasureTable")){
            val config = SConfigFile.getConfigFile(file.path)?: continue
            for(key in config.getKeys(false)){
                tmpTable[key]=TreasureTable(key,config.getConfigurationSection(key)!!)
            }
        }

        treasureTables.putAll(
                tmpTable.toList().sortedBy { it.second.priority }.toMap()
        )

    }

    fun loadContestName(){
        seasonContests.clear()
        seasonContests[Season.SPRING]= arrayListOf()
        seasonContests[Season.SUMMER]= arrayListOf()
        seasonContests[Season.AUTUMN]= arrayListOf()
        seasonContests[Season.WINTER]= arrayListOf()

        categorizedContests.clear()

        for(file in SConfigFile.getAllFileNameInPath(plugin.dataFolder.toString() + File.separator + "contests")){
            val config = SConfigFile.getConfigFile(file.path)?: continue
            config.getString("category")?.split(",")?.forEach {
                categorizedContests[it]?.add(file.nameWithoutExtension)?: kotlin.run {
                    categorizedContests[it]= arrayListOf(file.nameWithoutExtension)
                }
            }
            config.getString("season")?.split(",")?.forEach{
                val season=Season.stringToSeason(it)
                if(season!=Season.ALL&&season!=Season.ERROR) seasonContests[season]!!.add(file.nameWithoutExtension)
                if(season==Season.ALL){
                    seasonContests[Season.SPRING]!!.add(file.nameWithoutExtension)
                    seasonContests[Season.SUMMER]!!.add(file.nameWithoutExtension)
                    seasonContests[Season.AUTUMN]!!.add(file.nameWithoutExtension)
                    seasonContests[Season.WINTER]!!.add(file.nameWithoutExtension)
                }
            }?: kotlin.run {
                seasonContests[Season.SPRING]!!.add(file.nameWithoutExtension)
                seasonContests[Season.SUMMER]!!.add(file.nameWithoutExtension)
                seasonContests[Season.AUTUMN]!!.add(file.nameWithoutExtension)
                seasonContests[Season.WINTER]!!.add(file.nameWithoutExtension)
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

    fun pickTreasure(fisher: Player,hookLocation: Location):Treasure?{

        var broken=false
        for(region in Man10Fishing.regionContainer!![BukkitAdapter.adapt(fisher.world)]!!.regions) {
            Man10Fishing.treasureArea.forEach {
                if(region.key.startsWith(it)&&region.value.contains(BukkitAdapter.asBlockVector(hookLocation))){
                    broken=true
                    return@forEach
                }
            }
            if(broken)break
        }
        if(!broken)return null

        treasureTables.values.forEach {
            if(it.hasPermission(fisher))return it.getTreasure()
        }

        return null
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

        for (rarity in rarity.values.sortedByDescending { it.weight }){
            if(!rarity.enabledItemIndex) continue
            ItemIndex.itemIndexes[rarity.name] = ItemIndex.fromRarity(rarity)
        }
    }

    fun loadFishRecords(){
        fishRecords.clear()

        Thread{
            fish.keys.forEach {fishName->
                val rawMaxSizeResult=Man10Fishing.mysql.query("select uuid,size from fish_log as max_record join(select MAX(size) as maxsize from fish_log where fish='${fishName}') as sub_table on max_record.size=sub_table.maxsize limit 1;")
                if(rawMaxSizeResult.isEmpty())return@forEach
                val maxSizeResult=rawMaxSizeResult[0]
                val minSizeResult=Man10Fishing.mysql.query("select uuid,size from fish_log as min_record join(select MIN(size) as minsize from fish_log where fish='${fishName}') as sub_table on min_record.size=sub_table.minsize limit 1;")[0]
                val amountResult=Man10Fishing.mysql.query("select SUM(1) as amount from fish_log where fish='${fishName}';")[0]
                val firstFisherResult=Man10Fishing.mysql.query("select uuid from fish_log where fish='${fishName}' limit 1;")[0]
                fishRecords[fishName]=
                        FishRecordData(UUID.fromString(maxSizeResult.getString("uuid")),maxSizeResult.getDouble("size")
                                ,UUID.fromString(minSizeResult.getString("uuid")),minSizeResult.getDouble("size"),amountResult.getInt("amount")
                                ,UUID.fromString(firstFisherResult.getString("uuid")))
            }
        }.start()
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


    //季節に応じたスポーン場所を返す
    fun getSpawnLocation():Location?{
        return Man10Fishing.spawnPoints[Season.getCurrentSeason()]
    }

}