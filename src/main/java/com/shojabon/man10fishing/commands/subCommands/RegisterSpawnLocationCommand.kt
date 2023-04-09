package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.enums.Season
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RegisterSpawnLocationCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        val season= Season.valueOf(args[1].toUpperCase())
        val loc=sender.location

        plugin.config.set("spawnPoints.${season}",loc)
        Man10Fishing.spawnPoints[season]=loc
        plugin.saveConfig()

        sender.sendMessage("§e§l${season}§a§lのスポーン地点を現在の地点に変更しました")

        return true
    }
}