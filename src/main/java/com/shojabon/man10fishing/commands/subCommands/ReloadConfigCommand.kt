package com.shojabon.man10fishing.commands.subCommands;

import com.shojabon.man10fishing.Man10Fishing;
import com.shojabon.mcutils.Utils.MySQL.ThreadedMySQLAPI;
import com.shojabon.mcutils.Utils.SInventory.SInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCommand implements CommandExecutor {
    Man10Fishing plugin;

    public ReloadConfigCommand(Man10Fishing plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.reloadConfig();

        SInventory.closeAllSInventories();

        sender.sendMessage(Man10Fishing.prefix + "§a§lプラグインがリロードされました");
        return true;
    }
}
