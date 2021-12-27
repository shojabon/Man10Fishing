package com.shojabon.man10fishing

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.mcutils.Utils.SConfigFile
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import kotlin.math.sin
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
            if(alias == null || weight == 0){
                Bukkit.getLogger().info("レアリティ$rarityName でエラーが発生しました")
                continue
            }
            val rarityObject = FishRarity(rarityName, alias, weight)
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
            if(rand < checkingTotal-1) return rarity
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

            if(rand < checkingTotal-1) return fishObject
        }

        return null
    }

    //抽選のための魚テーブル作成 (内部名, 出現確立)
    fun createFishTable(fishAvailableToFish: ArrayList<Fish>, fisher: Player): HashMap<String, Float>{
        val result = HashMap<String, Float>()
        for(fish in fishAvailableToFish){
            result[fish.name] = fish.getRarityMultiplier(fisher, FishingRod(ItemStack(Material.FISHING_ROD)))
        }
        return result
    }

}