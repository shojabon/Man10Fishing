package com.shojabon.man10fishing

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.RegionContainer
import com.shojabon.man10fishing.commands.Man10FishingCommand
import com.shojabon.man10fishing.itemindex.ItemIndex
import com.shojabon.mcutils.Utils.MySQL.MySQLAPI
import com.shojabon.mcutils.Utils.MySQL.ThreadedMySQLAPI
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Man10Fishing : JavaPlugin() {

    companion object{
        lateinit var api: Man10FishingAPI
        lateinit var mysql : ThreadedMySQLAPI
        lateinit var instance : Man10Fishing
        var foodInRangeMultiplier: Int = 1
        lateinit var prefix: String

        //以下別plAPI
        var regionContainer : RegionContainer? = null
    }

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        instance = this
        api = Man10FishingAPI(this)
        mysql = ThreadedMySQLAPI(this)
        val file = File(dataFolder.toString() + File.separator + "fish" + File.separator)
        file.mkdir()
        Bukkit.getPluginManager().registerEvents(Man10FishingListener(this), this)
        foodInRangeMultiplier = config.getInt("foodInRangeMultiplier")
        prefix = config.getString("prefix")!!
        ItemIndex.loadData()

        regionContainer = if (server.pluginManager.isPluginEnabled("WorldGuard")){
            WorldGuard.getInstance().platform.regionContainer
        }else{
            logger.warning("WorldGuardが導入されていません！")
            null
        }

        val commandRouter = Man10FishingCommand(this)
        getCommand("mfish")!!.setExecutor(commandRouter)
        getCommand("mfish")!!.tabCompleter = commandRouter
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}