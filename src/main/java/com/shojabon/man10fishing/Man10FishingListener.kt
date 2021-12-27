package com.shojabon.man10fishing

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

class Man10FishingListener(private val plugin: Man10Fishing) : Listener {

    @EventHandler
    fun onFish(e: PlayerFishEvent) {
        if(e.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val pickedFish = Man10Fishing.api.pickFish(e.player)?: return
        e.caught?: return
        val fishedItem = e.caught as Item
        fishedItem.itemStack = pickedFish.item
        Bukkit.broadcastMessage(pickedFish.alias)

    }
}