package com.shojabon.man10fishing.commands

import com.shojabon.man10fishing.Man10Fishing
import org.bukkit.command.CommandExecutor
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandRouter
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandData
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandObject
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgument
import com.shojabon.man10fishing.commands.subCommands.ReloadConfigCommand

class Man10FishingCommand(var plugin: Man10Fishing) : SCommandRouter() {
    fun registerEvents() {
        setNoPermissionEvent { e: SCommandData -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lあなたは権限がありません") }
        setOnNoCommandFoundEvent { e: SCommandData -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lコマンドが存在しません") }
    }

    fun registerCommands() {

        //reload command
        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("reload")).addRequiredPermission("man10shopv2.reload")
                        .addExplanation("プラグインをリロードする")
                        .addExplanation("")
                        .addExplanation("設定を変更したときに使用する")
                        .addExplanation("コマンドを使用するとサーバー起動時状態に戻る")
                        .setExecutor(ReloadConfigCommand(plugin))
        )
    }

    init {
        registerCommands()
        registerEvents()
        pluginPrefix = Man10Fishing.prefix
    }
}