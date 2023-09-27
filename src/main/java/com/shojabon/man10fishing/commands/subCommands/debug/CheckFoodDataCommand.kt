package com.shojabon.man10fishing.commands.subCommands.debug

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishFood
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.floor

class CheckFoodDataCommand (var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val p: Player = sender

        val item=p.inventory.itemInMainHand
        if(!FishFood.isFood(item)) return true

        p.sendMessage("餌データ：${FishFood(item).getFoodTypeString()?:"データなし"}")

        return false
    }
}