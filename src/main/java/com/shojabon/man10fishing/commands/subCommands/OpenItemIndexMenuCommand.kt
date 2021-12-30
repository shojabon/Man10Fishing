package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.itemindex.ItemIndexInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OpenItemIndexMenuCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        if (!Man10FishingAPI.rarity.containsKey(args[1])){
            sender.sendMessage(Man10Fishing.prefix + "§4レアリティが存在しません")
            return false
        }
        ItemIndexInventory(args[1],plugin,sender.uniqueId).open(sender)
        return true
    }
}