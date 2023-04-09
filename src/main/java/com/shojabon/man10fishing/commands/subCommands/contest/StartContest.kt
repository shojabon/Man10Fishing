package com.shojabon.man10fishing.commands.subCommands.contest

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.contest.AbstractFishContest
import com.shojabon.man10fishing.contest.FishContestPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartContest(val plugin: Man10Fishing): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val contest = AbstractFishContest.newInstance(args[2])
        if (contest == null){
            sender.sendMessage(Man10Fishing.prefix + "§c§lコンテストが存在しません")
            return true
        }

        if (Man10Fishing.nowContest != null){
            sender.sendMessage(Man10Fishing.prefix + "§c§lコンテストが開始されています /mfish contest stopで終了してください")
            return true
        }

        //add players 参加の有無を選べるといいかもしれない
        Bukkit.getOnlinePlayers().forEach {
            contest.players[it.uniqueId] = FishContestPlayer(it.uniqueId, it.name)
        }

        contest.start()
        sender.sendMessage(Man10Fishing.prefix + "§a§lコンテストを開始しました")
        return true
    }
}