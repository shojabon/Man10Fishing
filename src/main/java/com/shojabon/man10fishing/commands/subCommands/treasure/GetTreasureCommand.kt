package com.shojabon.man10fishing.commands.subCommands.treasure

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GetTreasureCommand(var plugin: Man10Fishing) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) return true

        val treasure = Man10FishingAPI.treasure[args[2]]
        if (treasure == null) {
            sender.sendMessage("${Man10Fishing.prefix}§c${args[2]}は存在しません")
            return true
        }
        sender.inventory.addItem(treasure.getItem())

        return true
    }
}