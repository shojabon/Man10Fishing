package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import org.bukkit.command.CommandExecutor
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandRouter
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandData
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandObject
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgument
import com.shojabon.man10fishing.commands.subCommands.ReloadConfigCommand
import com.shojabon.mcutils.Utils.SConfigFile
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.File

class ReloadConfigCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.reloadConfig()
        SInventory.closeAllSInventories()
        Man10Fishing.foodConfig = SConfigFile.getConfigFile(plugin.dataFolder.toString() + File.separator + "foodConfig.yml")
        Man10FishingAPI.fish.clear()
        Man10FishingAPI.rarity.clear()
        Man10Fishing.api = Man10FishingAPI(plugin)
        Man10Fishing.api.loadSchedulers()
        Man10Fishing.api.loadItemIndexes()
        sender.sendMessage(Man10Fishing.prefix + "§a§lプラグインがリロードされました")
        return true
    }
}