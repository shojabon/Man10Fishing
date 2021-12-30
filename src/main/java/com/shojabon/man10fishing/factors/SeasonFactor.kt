package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

@Author(author = "JToTl")
@FishFactorDefinition(name = "季節",
        iconMaterial = Material.SUNFLOWER,
        explanation = ["釣れる季節"],
        adminSetting = false,
        settable = true)
/*
    season: start-end
    で指定
    例
    season: 2-4
 */
class SeasonFactor(fish: Fish) : FishFactor(fish) {

    val seasons= FishSettingVariable("season", listOf("1-12"))

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {
        //月を1~12で取得
        val month=Calendar.getInstance().get(Calendar.MONTH)+1
        for(season in seasons.get()){
            //開始と終了に分解
            val stAndEnd=season.split("-")
            val start=stAndEnd[0].toIntOrNull()
            val end=stAndEnd[1].toIntOrNull()

            if(start==null||end==null){
                Bukkit.getLogger().info("§4${fish.name}のseason読み取りでエラーが発生しました")
                return false
            }
            if(start<=month&&month<=end){
                return true
            }
        }
        return false
    }
}