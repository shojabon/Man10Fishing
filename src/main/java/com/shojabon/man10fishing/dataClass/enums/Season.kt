package com.shojabon.man10fishing.dataClass.enums

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.scheduler.FishingScheduler
import java.util.*
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
        //現在の季節を返す
        //基準は1990/1/4の日曜日
        //季節は、基準日から7日を春として1週間ごとに春→夏→秋→冬→春...と帰納的に定義される
        fun getCurrentSeason(): Season {
            return when((((Date().time/864000000L)+3L)/7)%4){
                0L->SPRING
                1L->SUMMER
                2L->AUTUMN
                else->WINTER
                //処理の関係上elseとなっているが、これは3Lの場合のみしか通らない
            }
        }
    }
}