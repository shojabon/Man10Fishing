package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ShowRanking(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (Man10Fishing.nowContest == null) return true
        val uuid=sender.uniqueId
        if(Man10Fishing.nowContest!!.hideRanking.contains(uuid))
        {
            Man10Fishing.nowContest!!.hideRanking.remove(uuid)
            Man10Fishing.nowContest!!.displayScoreboardRanking()
            sender.sendMessage("${Man10Fishing.prefix}§aランキングを表示します")
        }
        else {
            sender.sendMessage("${Man10Fishing.prefix}§a既に表示されています")
        }
        return true
    }
}