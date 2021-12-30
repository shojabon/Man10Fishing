package com.shojabon.man10fishing.itemindex

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.mcutils.Utils.SInventory.SInventory
import java.util.*
import kotlin.collections.HashMap

class ItemIndex(val name : String, plugin : Man10Fishing){

    companion object{
        val fishdexList = HashMap<UUID,HashMap<String,FishdexEntry>>()

        fun loadData(): Boolean {
            val rs = Man10Fishing.mysql.query("select * from fish_log;") ?: return false

            for (data in rs){
                val uuid = UUID.fromString(data.getString("uuid"))
                if (!fishdexList.containsKey(uuid)){
                    fishdexList[uuid] = hashMapOf()
                }

                fishdexList[uuid]!![data.getString("fish")] = FishdexEntry().setDataFromDB(data)
            }

            return true
        }
    }




}