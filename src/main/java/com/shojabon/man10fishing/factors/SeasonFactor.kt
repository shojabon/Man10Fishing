package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.enums.Season
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
    日曜日〜土曜日を１週間とした
 */
class SeasonFactor(fish: Fish) : FishFactor(fish) {

    val seasons= FishSettingVariable("season", listOf("ALL"))

    //季節を取得する関数はどこか一般用に作るべき
    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {

        //1970/1/4から何週間経ったかを/7までで計算し、その後4で割った余りを考えることで現在の季節を取得する
        val currentSeason=Man10Fishing.api.getCurrentSeason()

        for(season in seasons.get()){

            if(checkSeason(season,currentSeason)) return true

        }
        return false
    }


    //Stringで表された季節をLongに変換する
    private fun checkSeason(strSeason:String,current:Season):Boolean{
        val season=Season.valueOf(strSeason)
        return season==Season.ALL||season==current
    }
}