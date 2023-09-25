package com.shojabon.man10fishing

import ToolMenu.SingleItemStackSelectorMenu
import com.shojabon.man10fishing.contest.FishContestPlayer
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.dataClass.FishRarity
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.SizeRank
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class Man10FishingListener(private val plugin: Man10Fishing) : Listener {

    @EventHandler
    fun onFish(e: PlayerFishEvent) {
        if(e.state != PlayerFishEvent.State.CAUGHT_FISH) return

        val mainHand = e.player.inventory.itemInMainHand
        if(!FishingRod.isRod(mainHand)) return
        val rodItem = FishingRod(mainHand)
        if(rodItem.getFoodCount() <= 0) return

        e.caught?: return
        val pickedFish = Man10Fishing.api.pickFish(e.player,rodItem,e.hook.location)?: return



        val fishParameter: FishParameter = FishParameter().generateFishParameter(e.player, pickedFish)
        val fishedItem = e.caught as Item
        messageWithParameter(e.player,fishParameter)
        fishedItem.itemStack = pickedFish.getDetailedItem(fishParameter)
        pickedFish.executeOnFish(fishParameter, e.player, rodItem)

        if (!rodItem.removeFoodCount(1)){
            e.player.sendMessage(Man10Fishing.prefix + "§c餌がなくなりました")
        }
        rodItem.updateLore()
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
            return@setCheckItemFunction FishFood.isFood(itemStack)
        }
//        menu.selectTypeItem(true)
        menu.setOnConfirm { itemStack: ItemStack? ->
            if(e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return@setOnConfirm
            if(e.player.inventory.itemInMainHand.type != Material.FISHING_ROD) return@setOnConfirm
            if(!FishingRod.isRod(e.player.inventory.itemInMainHand)) return@setOnConfirm
            if(itemStack == null) return@setOnConfirm
            if(itemStack.type.isAir) return@setOnConfirm
            if(!FishFood.isFood(itemStack)) return@setOnConfirm

            e.player.inventory.removeItemAnySlot(itemStack)

            val rod = FishingRod(e.player.inventory.itemInMainHand)
            val foodType = FishFood(itemStack).getFoodTypeString() ?: return@setOnConfirm

            rod.setFoodCount(itemStack.amount)
            rod.setFoodType(foodType)
            rod.updateLore()
            e.player.sendMessage(Man10Fishing.prefix + "§a餌をセットしました")
            menu.close(e.player)

        }
        menu.open(e.player)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        val contest = Man10Fishing.nowContest?:return
        contest.onJoin(e)
    }

    private fun messageWithParameter(player: Player, parameter: FishParameter){
        if(messageRegardingOfSize(player,parameter))return
        if(messageRegardingOfRarity(player,parameter))return
    }

    private fun messageRegardingOfSize(player: Player, parameter: FishParameter):Boolean{
        val rarityData=Man10FishingAPI.rarity[parameter.fish.rarity]!!
        when(parameter.sizeRank){
            SizeRank.BIG->Man10Fishing.api.broadcastPlMessage("§f§l${player.name}§aが巨大サイズの${rarityData.namePrefix}${rarityData.loreDisplayName} ${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§aを釣り上げた!")
            SizeRank.SMALL->Man10Fishing.api.broadcastPlMessage("§f§l${player.name}§aがミニサイズの${rarityData.namePrefix}${rarityData.loreDisplayName} ${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§aを釣り上げた!")
            else -> {return false}
            }
        return true
        }

    private fun messageRegardingOfRarity(player: Player, parameter: FishParameter):Boolean{
        val rarityData=Man10FishingAPI.rarity[parameter.fish.rarity]!!
        if(Man10FishingAPI.broadcastRarity.contains(rarityData)){
            Man10Fishing.api.broadcastPlMessage("§f§l${player.name}が${rarityData.loreDisplayName}${rarityData.name} ${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§f§lを釣り上げた!")
            return true
        }
        player.sendMessage(Man10Fishing.prefix+"${rarityData.namePrefix}${rarityData.loreDisplayName} ${parameter.fish.alias}§e§l(§f${parameter.size}cm§e§l)§f§lを釣り上げた!")
        return false
    }
}