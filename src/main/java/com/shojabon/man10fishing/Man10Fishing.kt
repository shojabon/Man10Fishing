package com.shojabon.man10fishing

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.RegionContainer
import com.shojabon.man10fishing.commands.Man10FishingCommand
import com.shojabon.man10fishing.contest.AbstractFishContest
import com.shojabon.man10fishing.dataClass.enums.Season
import com.shojabon.man10fishing.itemindex.ItemIndexListener
import com.shojabon.man10fishing.scheduler.SchedulerManager
import com.shojabon.mcutils.Utils.MySQL.ThreadedMySQLAPI
import com.shojabon.mcutils.Utils.SConfigFile
import com.shojabon.mcutils.Utils.SInventory.SInventory
import com.shojabon.mcutils.Utils.SLongTextInput
import com.shojabon.mcutils.Utils.VaultAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Man10Fishing : JavaPlugin() {

    companion object{
        lateinit var api: Man10FishingAPI
        lateinit var mysql : ThreadedMySQLAPI
        lateinit var vault: VaultAPI
        lateinit var instance : Man10Fishing
        lateinit var foodConfig : ConfigurationSection
        var foodInRangeMultiplier: Int = 1
        lateinit var prefix: String

        var nowContest : AbstractFishContest? = null

        lateinit var schedulerManager: SchedulerManager

        //WorldGuard
        var regionContainer : RegionContainer? = null

        val spawnPoints=HashMap<Season, Location>()
    }

    fun loadConfig(){
        val file = File(dataFolder.toString() + File.separator + "fish" + File.separator)
        file.mkdir()
        Bukkit.getPluginManager().registerEvents(Man10FishingListener(this), this)
        Bukkit.getPluginManager().registerEvents(ItemIndexListener(), this)
        foodInRangeMultiplier = config.getInt("foodInRangeMultiplier")
        prefix = config.getString("prefix")!!

        if(!File(dataFolder.toString() + File.separator + "foodConfig.yml").exists()){
            SConfigFile(this).saveResource("foodConfig.yml", dataFolder.toString() + File.separator + "foodConfig.yml")
        }
        foodConfig = SConfigFile.getConfigFile(dataFolder.toString() + File.separator + "foodConfig.yml")

        regionContainer = if (server.pluginManager.isPluginEnabled("WorldGuard")){
            WorldGuard.getInstance().platform.regionContainer
        }else{
            logger.warning("WorldGuardが導入されていません！")
            null
        }

        //季節ごとのスポーン地点読み取り
        val configSection=config.getConfigurationSection("spawnPoints")
        spawnPoints.clear()
        for(strSeason in configSection?.getKeys(false)?: listOf()){
            val season=Season.valueOf(strSeason)
            val loc=configSection?.getLocation(strSeason)
            if(loc!=null){
                spawnPoints[season]=loc
            }
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        instance = this
        api = Man10FishingAPI(this)
        mysql = ThreadedMySQLAPI(this)
        vault = VaultAPI()
        api.createTables()

        loadConfig()


        schedulerManager = SchedulerManager()
        api.loadSchedulers()
        api.loadItemIndexes()
        schedulerManager.start()

        val commandRouter = Man10FishingCommand(this)
        getCommand("mfish")!!.setExecutor(commandRouter)
        getCommand("mfish")!!.tabCompleter = commandRouter



    }

    override fun onDisable() {
        // Plugin shutdown logic
        schedulerManager.interrupt()
        SInventory.closeAllSInventories()
    }
}