package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.contest.AbstractFishContest
import com.shojabon.man10fishing.contest.data.FishContestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class InfoContestCommand (val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (Man10Fishing.nowContest == null){
            sender.sendMessage(Man10Fishing.prefix + "§c§l現在コンテストは行われていません")
            return true
        }

        Man10Fishing.nowContest?.getContestInfoMessage()?.forEach {
            sender.sendMessage(it)
        }
        return true
    }
}