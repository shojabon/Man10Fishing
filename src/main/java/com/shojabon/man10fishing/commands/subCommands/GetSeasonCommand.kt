package com.shojabon.man10fishing.commands.subCommands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.dataClass.enums.Season
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GetSeasonCommand(var plugin: Man10Fishing) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        sender.sendMessage("§a§l今の季節は${translateSeason(Season.getCurrentSeason())}§a§lです")

        return true
    }

    private fun translateSeason(season: Season):String{
        return when(season){
            Season.SPRING->"§d§l春"
            Season.SUMMER->"§b§l夏"
            Season.AUTUMN->"§6§l秋"
            Season.WINTER->"§f§l冬"
            else->"§cエラー"
        }
    }
}