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
    season:
    - 季節
    - 季節
    ...
    で指定
    例
    season:
    - AUTUMN
    使えるものは[SPRING,SUMMER,AUTUMN,WINTER]
    それ以外の文字列を入れると全ての季節で釣れるようになる
 */
class  SeasonFactor(fish: Fish) : FishFactor(fish) {

    val seasons= FishSettingVariable("season", listOf("ALL"))

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {

        //1970/1/4から何週間経ったかを/7までで計算し、その後4で割った余りを考えることで現在の季節を取得する
        val nowSeason=(((Date().time/864000000L)-3L)/7)%4

        for(season in seasons.get()){

            if(representSeasonInLong(season,nowSeason)==nowSeason) return true

        }
        return false
    }


    //Stringで表された季節をLongに変換する
    private fun representSeasonInLong(season:String,nowSeason:Long):Long{
        return when(season){
            "SPRING"->0L
            "SUMMER"->1L
            "AUTUMN"->2L
            "WINTER"->3L
            else->-nowSeason
        }
    }
}