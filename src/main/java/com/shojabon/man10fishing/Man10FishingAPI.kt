package com.shojabon.man10fishing

import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.mcutils.Utils.SConfigFile
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
    }

    init {
        loadRarity()
        loadFish()
    }

    //　=========  コンフィグロード系統　=========

    //レアリティロード
    private fun loadRarity(){
        val configSection = plugin.config.getConfigurationSection("rarity") ?: return
        for(rarityName in configSection.getKeys(false)){
            val alias = configSection.getString("$rarityName.alias")
            val weight = configSection.getInt("$rarityName.weight")
            val material = Material.getMaterial(configSection.getString("$rarityName.material")?:"STONE")?:Material.STONE
            if(alias == null || weight == 0){
                Bukkit.getLogger().info("レアリティ$rarityName でエラーが発生しました")
                continue
            }
            val rarityObject = FishRarity(rarityName, alias, weight,material)
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
    fun pickFish(fisher: Player): Fish? {
        val rarity = pickRarity()?: return null
        val fishGroup = createFishTable(rarity.fishInGroup, fisher)

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
    fun createFishTable(fishAvailableToFish: ArrayList<Fish>, fisher: Player): HashMap<String, Float>{
        val result = HashMap<String, Float>()
        for(fish in fishAvailableToFish){
            if(!fish.isFishEnabled(fisher, FishingRod(ItemStack(Material.FISHING_ROD)))) continue
            result[fish.name] = fish.getRarityMultiplier(fisher, FishingRod(ItemStack(Material.FISHING_ROD)))
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