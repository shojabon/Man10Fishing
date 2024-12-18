package com.shojabon.man10fishing.commands

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.commands.subCommands.*
import com.shojabon.man10fishing.commands.subCommands.contest.*
import com.shojabon.man10fishing.commands.subCommands.debug.CheckFoodDataCommand
import com.shojabon.man10fishing.commands.subCommands.debug.CheckFoodTableCommand
import com.shojabon.man10fishing.commands.subCommands.debug.FillAllItemIndexCommand
import com.shojabon.man10fishing.commands.subCommands.debug.SellAverageCommand
import com.shojabon.man10fishing.commands.subCommands.fish.GetFishCommand
import com.shojabon.man10fishing.commands.subCommands.food.CreateFoodCommand
import com.shojabon.man10fishing.commands.subCommands.food.SetMixablityCommand
import com.shojabon.man10fishing.commands.subCommands.food.SynthesizeFishFoodCommand
import com.shojabon.man10fishing.commands.subCommands.rod.MakeIntoRodCommand
import com.shojabon.man10fishing.commands.subCommands.rod.SetValueCommand
import com.shojabon.man10fishing.commands.subCommands.treasure.GetTreasureCommand
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.man10fishing.itemindex.inventory.CreateItemIndex
import com.shojabon.man10fishing.itemindex.inventory.ItemIndexCategory
import com.shojabon.man10fishing.menu.FishSellMenu
import com.shojabon.mcutils.Utils.SCommandRouter.*
import org.bukkit.Sound
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

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("on"))
                        .addRequiredPermission("mfish.op")
                        .addExplanation("プラグインを有効化する")
                        .setExecutor { sender,_,_,_->
                            if(!Man10Fishing.enabled) {
                                Man10Fishing.enabled=true
                                plugin.config.set("enabled", true)
                                plugin.saveConfig()
                                sender.sendMessage("${Man10Fishing.prefix}§aオンにしました")
                            }else{
                                sender.sendMessage("${Man10Fishing.prefix}§c既にオンです")
                            }

                            return@setExecutor true
                        }
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("off"))
                        .addRequiredPermission("mfish.op")
                        .addExplanation("プラグインを停止する")
                        .setExecutor { sender,_,_,_->
                            if(Man10Fishing.enabled) {
                                Man10Fishing.enabled=false
                                plugin.config.set("enabled", false)
                                plugin.saveConfig()
                                sender.sendMessage("${Man10Fishing.prefix}§aオフにしました")
                            }else{
                                sender.sendMessage("${Man10Fishing.prefix}§c既にオフです")
                            }

                            return@setExecutor true
                        }
        )

        //itemindex command
        addCommand(
            SCommandObject()
                .addArgument(SCommandArgument().addAllowedString("ii"))
                .addRequiredPermission("man10fishing.ii")
                .addExplanation("図鑑を見る")
                .setExecutor { sender, _, _, _ ->
                    if (sender !is Player)return@setExecutor true
                    sender.playSound(sender.location, Sound.BLOCK_CHEST_OPEN,1f,1f)
                    ItemIndexCategory(plugin, sender.uniqueId).open(sender)
                    return@setExecutor true
                }
        )

        val itemIndexArgs = SCommandArgument().addAllowedType(SCommandArgumentType.STRING)
                .addAlias("内部名")

        ItemIndex.itemIndexes.forEach {
            if (it.value.fromRarity) return@forEach
            itemIndexArgs.addAlias(it.key)
        }

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("ii"))
                        .addArgument(SCommandArgument().addAllowedString("create"))
                        .addArgument(itemIndexArgs)
                        .addRequiredPermission("man10fishing.ii.create")
                        .addExplanation("図鑑を作成する")
                        .setExecutor { sender, _, _, args ->
                            if (sender !is Player)return@setExecutor true
                            val data = ItemIndex.itemIndexes[args[2]]
                            CreateItemIndex(args[2], data?: ItemIndex()).open(sender)
                            return@setExecutor true
                        }
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
                        .addArgument(SCommandArgument().addAllowedString("rod"))
                        .addArgument(SCommandArgument().addAllowedString("set"))
                        .addArgument(SCommandArgument().addAllowedString("season"))
                        .addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING))
                        .addRequiredPermission("man10fishing.rod.set")
                        .addExplanation("持っている釣り竿の季節変数を設定する")
                        .setExecutor(SetValueCommand(plugin))
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

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("food")).addArgument(SCommandArgument().addAllowedString("unmixable"))
                        .addRequiredPermission("man10fishing.food.unmiable")
                        .addExplanation("餌を合成不可にする")
                        .setExecutor(SetMixablityCommand(plugin))
        )

        val contestArgs = SCommandArgument()
        contestArgs.addAlias("コンテスト名")
        Man10Fishing.api.getContestList().forEach {
            contestArgs.addAlias(it)
        }

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("start"))
                        .addArgument(contestArgs)
                        .addRequiredPermission("man10fishing.contest.start")
                        .addExplanation("コンテストを開始する")
                        .setExecutor(StartContestCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("stop"))
                        .addRequiredPermission("man10fishing.contest.stop")
                        .addExplanation("コンテストを強制的に終了する")
                        .setExecutor(StopContestCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("info"))
                        .addRequiredPermission("man10fishing.contest.info")
                        .addExplanation("現在行われているコンテストの情報を見る")
                        .setExecutor(InfoContestCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("contest")).addArgument(SCommandArgument().addAllowedString("adminInfo"))
                        .addArgument(contestArgs)
                        .addRequiredPermission("man10fishing.contest.adminInfo")
                        .addExplanation("コンテストの内部情報を見る")
                        .setExecutor(AdminInfoContestCommand(plugin))
        )

        val fishArgs = SCommandArgument()
        fishArgs.addAlias("魚名")
        Man10FishingAPI.fish.keys.forEach {
            fishArgs.addAlias(it)
        }

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("fish"))
                        .addArgument(SCommandArgument().addAllowedString("get"))
                        .addArgument(fishArgs)
                        .addRequiredPermission("man10fishing.fish.get")
                        .addExplanation("魚を取得する")
                        .setExecutor(GetFishCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("spawn"))
                        .addExplanation("スポーン地点に戻る")
                        .addRequiredPermission("man10fishing.spawn")
                        .setExecutor(SpawnCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("registerSpawn"))
                        .addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING))
                        .addExplanation("季節のスポーン地点を設定する")
                        .addRequiredPermission("man10fishing.registerspawn")
                        .setExecutor(RegisterSpawnLocationCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("hideRanking"))
                        .addExplanation("ランキングを非表示にする")
                        .addRequiredPermission("man10fishing.hideranking")
                        .setExecutor(HideRankingCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("showRanking"))
                        .addExplanation("ランキングを表示する")
                        .addRequiredPermission("man10fishing.showranking")
                        .setExecutor(ShowRankingCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("sell"))
                        .addExplanation("魚を売る")
                        .addRequiredPermission("man10fishing.sell")
                        .setExecutor { sender, _, _, _ ->
                            if (sender !is Player)return@setExecutor true
                            FishSellMenu().open(sender)
                            return@setExecutor true
                        }
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("debug")).addArgument(SCommandArgument().addAllowedString("checkFoodTable"))
                        .addExplanation("餌で釣れやすくなる魚をチェックする")
                        .addRequiredPermission("man10fishing.debug")
                        .setExecutor(CheckFoodTableCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("debug")).addArgument(SCommandArgument().addAllowedString("checkFoodData"))
                        .addExplanation("餌の内部値をチェックする")
                        .addRequiredPermission("man10fishing.debug")
                        .setExecutor(CheckFoodDataCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("debug")).addArgument(SCommandArgument().addAllowedString("sellAverage"))
                        .addExplanation("売却額の平均をチェックする")
                        .addRequiredPermission("man10fishing.debug")
                        .setExecutor(SellAverageCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("treasure"))
                        .addArgument(SCommandArgument().addAllowedString("get"))
                        .addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING))
                        .addRequiredPermission("man10fishing.treasure.get")
                        .addExplanation("お宝を取得する")
                        .setExecutor(GetTreasureCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("debug")).addArgument(SCommandArgument().addAllowedString("fillAllItemIndex"))
                        .addExplanation("全ての魚の図鑑を埋める")
                        .addRequiredPermission("man10fishing.debug")
                        .setExecutor(FillAllItemIndexCommand(plugin))
        )

        addCommand(
                SCommandObject()
                        .addArgument(SCommandArgument().addAllowedString("season"))
                        .addExplanation("現在の季節を表示する")
                        .setExecutor(GetSeasonCommand(plugin))
        )

    }

    init {
        registerCommands()
        registerEvents()
        pluginPrefix = Man10Fishing.prefix
    }
}