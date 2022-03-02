package com.shojabon.man10fishing.commands.subCommands.food

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.menu.SynthesizeFishFoodMenu
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SynthesizeFishFoodCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        val p: Player = sender
        val menu = SynthesizeFishFoodMenu()
        menu.open(p)
        return false
    }
}