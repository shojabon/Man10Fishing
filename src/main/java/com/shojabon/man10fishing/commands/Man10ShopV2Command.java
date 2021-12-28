package com.shojabon.man10fishing.commands;

import com.shojabon.man10fishing.Man10Fishing;
import com.shojabon.man10fishing.commands.subCommands.ReloadConfigCommand;
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgument;
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandArgumentType;
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandObject;
import com.shojabon.mcutils.Utils.SCommandRouter.SCommandRouter;


public class Man10ShopV2Command extends SCommandRouter {

    Man10Fishing plugin;

    public Man10ShopV2Command(Man10Fishing plugin){
        this.plugin = plugin;
        registerCommands();
        registerEvents();
        pluginPrefix = Man10Fishing.prefix;
    }

    public void registerEvents(){
        setNoPermissionEvent(e -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lあなたは権限がありません"));
        setOnNoCommandFoundEvent(e -> e.sender.sendMessage(Man10Fishing.prefix + "§c§lコマンドが存在しません"));
    }

    public void registerCommands(){

        //reload command

        addCommand(
                new SCommandObject()
                        .addArgument(new SCommandArgument().addAllowedString("reload")).

                        addRequiredPermission("man10shopv2.reload")
                        .addExplanation("プラグインをリロードする")
                        .addExplanation("")
                        .addExplanation("設定を変更したときに使用する")
                        .addExplanation("コマンドを使用するとサーバー起動時状態に戻る")
                        .setExecutor(new ReloadConfigCommand(plugin))
        );

    }

}
