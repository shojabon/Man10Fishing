package com.shojabon.man10fishing

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Man10Fishing : JavaPlugin() {

    companion object{
        lateinit var api: Man10FishingAPI
        var foodInRangeMultiplier: Int = 1
    }

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        api = Man10FishingAPI(this)
        val file = File(dataFolder.toString() + File.separator + "fish" + File.separator)
        file.mkdir()
        Bukkit.getPluginManager().registerEvents(Man10FishingListener(this), this)
        foodInRangeMultiplier = config.getInt("foodInRangeMultiplier")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}