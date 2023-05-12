package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HideRanking(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return true
        if (Man10Fishing.nowContest == null) return true
        val uuid=sender.uniqueId
        if(Man10Fishing.nowContest!!.hideRanking.contains(uuid))
        {
            sender.sendMessage("${Man10Fishing.prefix}§a既に非表示です")
        }
        else {
            Man10Fishing.nowContest!!.hideRanking.add(uuid)
            sender.scoreboard = Bukkit.getScoreboardManager().newScoreboard
            sender.sendMessage("${Man10Fishing.prefix}§aランキングを非表示にします")
        }
        return true
    }
}