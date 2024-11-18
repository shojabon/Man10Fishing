package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.mcutils.Utils.SConfigFile
import com.shojabon.mcutils.Utils.SInventory.SInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File

class OffCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        if(Man10Fishing.enabled) {
            Man10Fishing.enabled=false
            plugin.config.set("enabled", false)
            plugin.saveConfig()
            sender.sendMessage("${Man10Fishing.prefix}§aオフにしました")
        }else{
            sender.sendMessage("${Man10Fishing.prefix}§c既にオフです")
        }

        return true
    }
}