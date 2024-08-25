package com.shojabon.man10fishing.commands.subCommands.debug

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.dataClass.FishParameter
import com.shojabon.man10fishing.itemindex.ItemIndex
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FillAllItemIndexCommand(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        Man10FishingAPI.fish.forEach {
            ItemIndex.fishdexList[sender.uniqueId]!![it.key] = arrayListOf(FishParameter().generateFishParameter(
                sender, it.value
            ))
        }

        return true
    }
}