package com.shojabon.man10fishing.dataClass

import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.enums.SizeRank
import com.shojabon.mcutils.Utils.MySQL.MySQLCachedResultSet
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

class FishParameter {

    lateinit var fish : Fish
    lateinit var name : String
    lateinit var uuid : UUID
    var size : Double = 0.0
    lateinit var dateTime : Date
    lateinit var sizeRank: SizeRank


    var loaded = false




    fun setDataFromDB(resultSet: MySQLCachedResultSet): FishParameter {
        fish = Man10FishingAPI.fish[resultSet.getString("fish")]?:return this
        name = resultSet.getString("name")?:return this
        uuid = UUID.fromString(resultSet.getString("uuid"))?:return this
        size = resultSet.getDouble("size")

        val dateFormat = resultSet.getString("date_time").replace("T"," ").replace("-","/")
        val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val date = sdFormat.parse(dateFormat)
        dateTime = date


        loaded = true
        return this
    }

    fun generateFishParameter(fisher: Player, fish: Fish): FishParameter {
        this.fish = fish
        name = fisher.name
        uuid = fisher.uniqueId
        val rawSize=fish.sizeFactor.generateRandomSize()
        size = rawSize.first
        sizeRank=rawSize.second

        dateTime = Date()

        loaded = true
        return this
    }


    // ふぉーまっと、きめよう
    fun generateIndexItem() : SInventoryItem?{
        if (!loaded)return null
        return SInventoryItem(fish.item)
    }

}