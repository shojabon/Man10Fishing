package com.shojabon.man10fishing

import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.dataClass.FishingRod
import org.bukkit.Material
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

        val mainHand = e.player.inventory.itemInMainHand
        if(!FishingRod.isRod(mainHand)) return

        val fishedItem = e.caught as Item
        fishedItem.itemStack = pickedFish.item
        val fishParameter: FishParameter = FishParameter().generateFishParameter(e.player, pickedFish)
        pickedFish.executeOnFish(fishParameter, e.player, FishingRod(mainHand))
    }
}