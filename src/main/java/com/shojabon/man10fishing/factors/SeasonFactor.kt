package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.dataClass.enums.Season
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "季節",
        iconMaterial = Material.SUNFLOWER,
        explanation = ["釣れる季節"],
        adminSetting = false,
        settable = true)
/*
    season:
    - 季節
    - 季節
    ...
    で指定
    例
    season:
    - AUTUMN
    使えるものは[SPRING,SUMMER,AUTUMN,WINTER,ALL]
    未指定だとALL
    日曜日〜土曜日を１週間とした
 */
class SeasonFactor(fish: Fish) : FishFactor(fish) {

    val seasons= FishSettingVariable("season", listOf("ALL"))

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod,hookLocation: Location): Boolean {

        var currentSeason=Season.getCurrentSeason()
        if(Man10Fishing.nowContest==null){
            if(rod.season in listOf(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER))currentSeason=rod.season
        }

        for(season in seasons.get()){

            if(checkSeason(season,currentSeason)) return true

        }
        return false
    }


    private fun checkSeason(strSeason:String,current:Season):Boolean{
        val season=Season.stringToSeason(strSeason)
        return season==Season.ALL||season==current
    }
}