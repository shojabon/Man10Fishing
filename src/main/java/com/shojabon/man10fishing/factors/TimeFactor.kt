package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing.Companion.foodInRangeMultiplier
import com.shojabon.man10fishing.annotations.Author
import org.bukkit.Material
import com.shojabon.man10fishing.dataClass.FishFactor
import org.bukkit.entity.Player
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import org.bukkit.Bukkit
import java.util.*
import kotlin.math.pow

@Author(author = "JToTl")
@FishFactorDefinition(name = "時間",
        iconMaterial = Material.CLOCK,
        explanation = ["釣れる時間の定義"],
        settable = true)
class TimeFactor(fish: Fish) : FishFactor(fish) {

    var timesOfDay = FishSettingVariable("time", listOf("0000-2359"))

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {
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