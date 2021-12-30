package com.shojabon.man10fishing.itemindex

import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.mcutils.Utils.MySQL.MySQLCachedResultSet
import com.shojabon.mcutils.Utils.SInventory.SInventoryItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FishdexEntry(){

    lateinit var fish : Fish
    lateinit var name : String
    lateinit var uuid : UUID
    var weight : Double = 0.0
    var size : Double = 0.0
    lateinit var dateTime : Date


    var loaded = false

    companion object{

    }




    fun setDataFromDB(resultSet: MySQLCachedResultSet): FishdexEntry {
        fish = Man10FishingAPI.fish[resultSet.getString("fish")]?:return this
        name = resultSet.getString("name")?:return this
        uuid = UUID.fromString(resultSet.getString("uuid"))?:return this
        weight = resultSet.getDouble("weight")
        size = resultSet.getDouble("size")

        val dateFormat = resultSet.getString("date_time").replace("T"," ").replace("-","/")
        val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val date = sdFormat.parse(dateFormat)
        dateTime = date


        loaded = true
        return this
    }


    // ふぉーまっと、きめよう
    fun generateFish() : SInventoryItem?{
        if (!loaded)return null
        return SInventoryItem(fish.item)
    }

}