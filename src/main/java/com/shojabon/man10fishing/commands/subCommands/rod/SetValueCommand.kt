package com.shojabon.man10fishing.commands.subCommands.rod

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetValueCommand (var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val p: Player = sender
        val item=p.inventory.itemInMainHand
        if (!FishingRod.isRod(item)) {
            sender.sendMessage(Man10Fishing.prefix + "§4Man10Fishの釣り竿を持ってください")
            return false
        }
        //サイズは保証されている
        val season= Season.stringToSeason(args[3])
        if(season!=Season.ERROR){
            FishingRod(item).season=season
            sender.sendMessage(Man10Fishing.prefix + "§aseasonを${season}にセットしました")
        }
        else{
            sender.sendMessage(Man10Fishing.prefix + "§4${args[3]}は存在しません")
        }

        return true
    }
}