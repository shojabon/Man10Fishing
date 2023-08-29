package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        val loc=Man10Fishing.api.getSpawnLocation()?:return true

        sender.teleport(loc)
        
        return true
    }
}