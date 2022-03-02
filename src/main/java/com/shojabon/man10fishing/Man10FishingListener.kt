package com.shojabon.man10fishing

import ToolMenu.SingleItemStackSelectorMenu
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.dataClass.FishingRod
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Man10FishingListener(private val plugin: Man10Fishing) : Listener {

    @EventHandler
    fun onFish(e: PlayerFishEvent) {
        if(e.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val pickedFish = Man10Fishing.api.pickFish(e.player)?: return
        e.caught?: return

        val mainHand = e.player.inventory.itemInMainHand
        if(!FishingRod.isRod(mainHand)) return
        val rodItem = FishingRod(mainHand)
        if(rodItem.getFoodCount() <= 0) return

        val fishedItem = e.caught as Item
        fishedItem.itemStack = pickedFish.item
        val fishParameter: FishParameter = FishParameter().generateFishParameter(e.player, pickedFish)
        pickedFish.executeOnFish(fishParameter, e.player, rodItem)
        rodItem.removeFoodCount(1)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        if(e.action != Action.LEFT_CLICK_BLOCK)return
        if(e.player.inventory.itemInMainHand.type != Material.FISHING_ROD) return
        if(!FishingRod.isRod(e.player.inventory.itemInMainHand)) return
        e.isCancelled = true

        val menu = SingleItemStackSelectorMenu("餌を選択してください", ItemStack(Material.AIR), plugin)
        menu.setCheckItemFunction { itemStack: ItemStack? ->
            if(itemStack == null) return@setCheckItemFunction false
            if(!FishFood.isFood(itemStack)) return@setCheckItemFunction false

            return@setCheckItemFunction true
        }
        menu.selectTypeItem(true)
        menu.setOnConfirm { itemStack: ItemStack? ->
            if(e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return@setOnConfirm
            if(e.player.inventory.itemInMainHand.type != Material.FISHING_ROD) return@setOnConfirm
            if(!FishingRod.isRod(e.player.inventory.itemInMainHand)) return@setOnConfirm
            if(itemStack == null) return@setOnConfirm

            if(!FishFood.isFood(itemStack)) return@setOnConfirm

            e.player.inventory.removeItemAnySlot(itemStack)

            val rod = FishingRod(e.player.inventory.itemInMainHand)
            val foodType = FishFood(itemStack).getFoodTypeString() ?: return@setOnConfirm
            Bukkit.broadcastMessage(foodType)
            rod.setFoodCount(5)
            rod.setFoodType(foodType)
            menu.close(e.player)

        }
        menu.open(e.player)
    }
}