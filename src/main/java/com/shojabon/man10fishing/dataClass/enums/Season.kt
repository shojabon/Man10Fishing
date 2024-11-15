package com.shojabon.man10fishing.dataClass.enums

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.scheduler.FishingScheduler
import java.util.logging.Level

enum class Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER,
    ERROR,
    NONE,
    ALL;

    companion object{
        fun stringToSeason(string: String):Season{
            return try {
                Season.valueOf((string).toUpperCase())
            } catch (e: Exception){
                Man10Fishing.instance.logger.log(Level.WARNING,"${Man10Fishing.prefix}§c${string}は有効な季節ではありません")
                ERROR
            }
        }
    }
}