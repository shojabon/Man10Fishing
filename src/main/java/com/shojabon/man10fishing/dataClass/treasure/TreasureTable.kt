package com.shojabon.man10fishing.dataClass.treasure

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import kotlin.properties.Delegates
import kotlin.random.Random

class TreasureTable(val key:String,configurationSection: ConfigurationSection) {



    private val permission=configurationSection.getString("permission","")!!
    val priority=configurationSection.getInt("priority")
    private val chanceTable=HashMap<Treasure,Int>()
    private var totalWeight=0

    companion object{

    }

    init {

        configurationSection.getConfigurationSection("treasures")?.getKeys(false)?.forEach {key->
            Man10FishingAPI.treasure[key]?.let {treasure ->
            chanceTable[treasure]=configurationSection.getInt("treasures.${key}")
            }?: kotlin.run {
                Bukkit.getLogger().info("トレジャー${key}は存在しません")
            }
        }

        chanceTable.values.forEach {
            totalWeight+=it
        }

//        chanceTable.entries.sortedBy { it.value }.toHashSet()


    }


    fun getTreasure():Treasure{
        var num= Random.nextInt(totalWeight)
        chanceTable.keys.forEach{
            num-=chanceTable[it]!!
            if(num<0){
                return it
            }
        }
        return chanceTable.keys.last()
    }

    fun hasPermission(player:Player):Boolean{
        return player.hasPermission(permission)
    }






}