package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import com.shojabon.man10fishing.annotations.Author
import org.bukkit.Material
import org.bukkit.entity.Player
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*
import kotlin.math.pow

@Author(author = "JToTl")
@FishFactorDefinition(name = "時間",
        iconMaterial = Material.CLOCK,
        explanation = ["釣れる時間の定義"],
        adminSetting = false,
        settable = true)
/*
    time: 0210-0340,1540-1902
    のように指定
    上記の場合、午前2時10分〜午前3時40分と午後3時40分〜午後7時2分
 */
class TimeFactor(fish: Fish) : FishFactor(fish) {

    var timesOfDay = FishSettingVariable("time", listOf("0000-2359"))

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod,hookLocation: Location): Boolean {
        val time=getTime()
        for(timeSlot in timesOfDay.get()){
            //時刻を開始と終了で分ける
            val stAndEnd=timeSlot.split("-")
            val start=stAndEnd[0].toIntOrNull()
            val end=stAndEnd[1].toIntOrNull()
            if(start==null||end==null){
                Bukkit.getLogger().info("§4${fish.name}の時間帯読み込み時にエラーが発生しました")
                continue
            }
            if(start<=time&&time<=end){
                return true
            }
        }
        return false
    }

    //現在時刻を取得 午後11時23分→2323
    private fun getTime():Int{
        val time=((Date().time+32400000)%86400000)/60000
        return ((time/60)*100+time%60).toInt()
    }

}