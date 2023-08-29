package com.shojabon.man10fishing.itemindex

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ItemIndexListener: Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if (ItemIndex.fishdexList.containsKey(e.player.uniqueId)) return
        Man10Fishing.mysql.asyncQuery("select * from fish_log where uuid = '${e.player.uniqueId}'") {
            val uuid = e.player.uniqueId

            if (!ItemIndex.fishdexList.containsKey(uuid)){
                ItemIndex.fishdexList[uuid] = hashMapOf()
            }

            for (data in it){
                if (!ItemIndex.fishdexList[uuid]!!.containsKey(data.getString("fish"))){
                    ItemIndex.fishdexList[uuid]!![data.getString("fish")] = arrayListOf()
                }
                ItemIndex.fishdexList[uuid]!![data.getString("fish")]!!.add(FishParameter().setDataFromDB(data))
            }
        }

    }
}