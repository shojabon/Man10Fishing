package com.shojabon.man10fishing.commands.subCommands.rod

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.itemindex.ItemIndexCategory
import com.shojabon.man10fishing.itemindex.ItemIndexInventory
import com.shojabon.mcutils.Utils.SItemStack
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MakeIntoRodCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)return true
        val p: Player = sender
        if(p.inventory.itemInMainHand.type != Material.FISHING_ROD){
            sender.sendMessage(Man10Fishing.prefix + "§4釣り竿を持ってください")
            return false
        }
        if(FishingRod.isRod(p.inventory.itemInMainHand)){
            sender.sendMessage(Man10Fishing.prefix + "§4すでにMan10Fishing釣り竿です")
            return false
        }
        val item = SItemStack(p.inventory.itemInMainHand)
        item.setCustomData(Man10Fishing.instance, "foodCount", "0")
        item.setCustomData(Man10Fishing.instance, "foodType", "0|0|0|0|0")
        sender.sendMessage(Man10Fishing.prefix + "§a釣り竿を作成しました")
        return true
    }
}