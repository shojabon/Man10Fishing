package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class InfoContest(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val config = try {
            YamlConfiguration.loadConfiguration(File("${Man10Fishing.instance.dataFolder.path}/contests/${args[2]}.yml"))
        } catch (e: IllegalArgumentException){
            sender.sendMessage("${Man10Fishing.prefix}§c§lコンテストが存在しません")
            return true
        }

        sender.sendMessage("${Man10Fishing.prefix}§a§lコンテスト情報")
        config.getKeys(false).forEach {
            sender.sendMessage("§a§l${it}: §f§l${config.get(it)}")
        }

        return true
    }
}