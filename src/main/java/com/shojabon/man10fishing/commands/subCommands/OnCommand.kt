package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class OnCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if(!Man10Fishing.enabled) {
            Man10Fishing.enabled=true
            plugin.config.set("enabled", true)
            plugin.saveConfig()
            sender.sendMessage("${Man10Fishing.prefix}§aオンにしました")
        }else{
            sender.sendMessage("${Man10Fishing.prefix}§c既にオンです")
        }

        return true
    }
}