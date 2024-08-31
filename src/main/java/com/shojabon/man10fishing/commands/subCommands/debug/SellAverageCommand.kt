package com.shojabon.man10fishing.commands.subCommands.debug

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishFood
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.floor

class SellAverageCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        var weightSum=0.0
        var average=0.0

        Man10FishingAPI.rarity.values.forEach {
            weightSum+=it.weight.toDouble()
        }

        Man10FishingAPI.rarity.values.forEach {
            average+=(it.minSellPrice+it.priceMultiplier*50.0)*it.weight.toDouble()/weightSum
        }

        sender.sendMessage("${Man10Fishing.prefix}一匹あたりの売却額平均は${floor(average*100)/100}円")

        return false
    }
}