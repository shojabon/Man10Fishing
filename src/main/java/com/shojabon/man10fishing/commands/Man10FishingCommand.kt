package com.shojabon.man10fishing.commands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.commands.subCommands.OpenItemIndexMenuCommand
import com.shojabon.man10fishing.commands.subCommands.RegisterSpawnLocationCommand
import com.shojabon.man10fishing.commands.subCommands.ReloadConfigCommand
import com.shojabon.man10fishing.commands.subCommands.SpawnCommand
import com.shojabon.man10fishing.commands.subCommands.contest.StartContest
import com.shojabon.man10fishing.commands.subCommands.contest.StopContest
import com.shojabon.man10fishing.commands.subCommands.food.CreateFoodCommand
import com.shojabon.man10fishing.commands.subCommands.food.SynthesizeFishFoodCommand
import com.shojabon.man10fishing.commands.subCommands.rod.MakeIntoRodCommand
import com.shojabon.man10fishing.itemindex.ItemIndexInventory
import com.shojabon.mcutils.Utils.SCommandRouter.*
import org.bukkit.entity.Player

class Man10FishingCommand(var plugin: Man10Fishing) : SCommandRouter() {
    fun registerEvents() {
        setNoPermissionEvent { e: SCommandData -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lあなたは権限がありません") }
        setOnNoCommandFoundEvent { e: SCommandData -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lコマンドが存在しません") }
    }

    fun registerCommands() {

        //reload command
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("reload")).addRequiredPermission("man10fishing.reload")
                .addExplanation("プラグインをリロードする")
                .addExplanation("")
                .addExplanation("設定を変更したときに使用する")
                .addExplanation("コマンドを使用するとサーバー起動時状態に戻る")
                .setExecutor(ReloadConfigCommand(plugin))
        )

        //itemindex command
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("ii"))
                .addRequiredPermission("man10fishing.ii")
                .addExplanation("図鑑を見る")
                .setExecutor { sender, _, _, _ ->
                    if (sender !is Player)return@setExecutor true
                    ItemIndexInventory("all", plugin, sender.uniqueId, false).open(sender)
                    return@setExecutor true
                }
        )

        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("ii"))
                .addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING).addAlias("レアリティ名").addAlias("list"))
                .addRequiredPermission("man10fishing.ii")
                .addExplanation("図鑑を見る")
                .setExecutor(OpenItemIndexMenuCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("rod")).addArgument(SCommandArgument().addAllowedString("create"))
                        .addRequiredPermission("man10fishing.rod.create")
                        .addExplanation("持っている釣り竿をMan10Fishingで使用可能にする")
                        .setExecutor(MakeIntoRodCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("food")).addArgument(SCommandArgument().addAllowedString("create"))
                        .addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING).addAlias("食べ物情報"))
                        .addRequiredPermission("man10fishing.food.create")
                        .addExplanation("餌を作成する 0|0|0|0|0形式で入力")
                        .setExecutor(CreateFoodCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("food")).addArgument(SCommandArgument().addAllowedString("mix"))
                        .addRequiredPermission("man10fishing.food.mix")
                        .addExplanation("餌を合成する")
                        .setExecutor(SynthesizeFishFoodCommand(plugin))
        )

        val contestArgs = SCommandArgument()
        Man10Fishing.api.getContestList().forEach {
            contestArgs.addAllowedString(it)
        }

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("start"))
                        .addArgument(contestArgs)
                        .addRequiredPermission("man10fishing.contest.start")
                        .addExplanation("コンテストを開始する")
                        .setExecutor(StartContest(plugin))
        )

        addCommand(
                SCommandObject()
                           .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("stop"))
                            .addRequiredPermission("man10fishing.contest.stop")
                            .addExplanation("コンテストを強制的に終了する")
                            .setExecutor(StopContest(plugin))
        )


        //spawn command
        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("spawn"))
                        .addRequiredPermission("man10fishing.spawn")
                        .addExplanation("釣りのスポーン地点へ戻る")
                        .setExecutor(SpawnCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("register"))
                        .addRequiredPermission("man10fishing.register.spawn")
                        .addExplanation("釣りのスポーン地点を設定する")
                        .setExecutor(RegisterSpawnLocationCommand(plugin))
        )
    }

    init {
        registerCommands()
        registerEvents()
        pluginPrefix = Man10Fishing.prefix
    }
}