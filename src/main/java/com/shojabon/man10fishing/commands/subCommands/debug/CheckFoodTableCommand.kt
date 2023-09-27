package com.shojabon.man10fishing.commands.subCommands.debug

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.menu.SynthesizeFishFoodMenu
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.floor
import kotlin.math.pow

class CheckFoodTableCommand (var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val p: Player = sender

        val item=p.inventory.itemInMainHand
        if(!FishFood.isFood(item)) return true

        val dummyRod=getDummyRod(FishFood(item))

        p.sendMessage("この餌で釣れやすくなる魚一覧")

        var count=0

        for(fish in Man10FishingAPI.fish.values){
            if(fish.foodFactor.rarityMultiplier(fish,1.0F,sender,dummyRod)!=1.0F){
                count++
                p.sendMessage("${fish.name}:${fish.alias}")
            }
        }

        p.sendMessage("全体の${floor((1000*count.toDouble()/Man10FishingAPI.fish.size.toDouble()))/10}%")

        return false
    }


    private fun getDummyRod(food:FishFood):FishingRod{
        val item = SItemStack(Material.FISHING_ROD)
        item.setCustomData(Man10Fishing.instance, "foodCount", "1")
        item.setCustomData(Man10Fishing.instance, "foodType", food.getFoodTypeString())
        return FishingRod(item.build())
    }

}