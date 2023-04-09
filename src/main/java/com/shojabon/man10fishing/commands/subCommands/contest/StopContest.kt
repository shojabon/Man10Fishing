package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StopContest(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (Man10Fishing.nowContest == null){
            sender.sendMessage(Man10Fishing.prefix + "§c§lコンテストが開始されていません")
            return true
        }
        Man10Fishing.nowContest!!.end()
        sender.sendMessage(Man10Fishing.prefix + "§a§lコンテストを終了しました")
        return true
    }
}