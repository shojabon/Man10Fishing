package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.CommandExecutor
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandRouter
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandData
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandObject
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgument
import com.shojabon.man10fishing.commands.subCommands.ReloadConfigCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class ReloadConfigCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.reloadConfig()
        SInventory.closeAllSInventories()
        sender.sendMessage(Man10Fishing.prefix + "§a§lプラグインがリロードされました")
        return true
    }
}