package com.shojabon.man10fishing.commands.subCommands.food

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishFood
import com.shojabon.mcutils.Utils.BaseUtils
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateFoodCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        val p: Player = sender
        val foodInformation = args[2]
        val foodSeparatedInformation = foodInformation.split("|")
        if(foodSeparatedInformation.size != 6){
            sender.sendMessage(Man10Fishing.prefix + "§4 餌情報は0|0|0|0|0|0形式で入力してください")
            return false
        }
        for(foodInfo in foodSeparatedInformation){
            if(!BaseUtils.isDouble(foodInfo)){
                sender.sendMessage(Man10Fishing.prefix + "§4 餌情報は0|0|0|0|0|0形式で入力してください")
                return false
            }
        }
        val item = SItemStack(p.inventory.itemInMainHand)
                .setLore(FishFood.getFoodTypeLoreFromStr(foodSeparatedInformation))
                .setDisplayName(FishFood.getFoodTypeNameFromStr(foodSeparatedInformation))
        item.setCustomData(Man10Fishing.instance, "foodType", foodInformation)
        sender.sendMessage(Man10Fishing.prefix + "§a餌を作成しました")
        return true
    }
}