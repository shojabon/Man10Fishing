package com.shojabon.man10fishing.itemindex

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.mcutils.Utils.SInventory.SInventory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ItemIndex{

    companion object{
        val fishdexList = HashMap<UUID,HashMap<String,FishParameter>>()

        fun loadData(): Boolean {
            val rs = Man10Fishing.mysql.query("select * from fish_log;") ?: return false

            for (data in rs){
                val uuid = UUID.fromString(data.getString("uuid"))
                if (!fishdexList.containsKey(uuid)){
                    fishdexList[uuid] = hashMapOf()
                }

                val dateFormat = data.getString("date_time").replace("T"," ").replace("-","/")
                val sdFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
                val date = sdFormat.parse(dateFormat)
                val fishdexDate = fishdexList[uuid]!![data.getString("fish")]?.dateTime
                if (fishdexDate == null || date.before(fishdexDate)){
                    fishdexList[uuid]!![data.getString("fish")] = FishParameter().setDataFromDB(data)
                }


            }

            return true
        }
    }




}