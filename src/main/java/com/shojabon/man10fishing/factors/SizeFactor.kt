package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import com.shojabon.man10fishing.dataClass.enums.SizeRank
import org.bukkit.Material
import kotlin.math.round

@FishFactorDefinition(name = "サイズ設定",
        iconMaterial = Material.CHEST,
        explanation = ["サイズ定義"],
        adminSetting = false,
        settable = false)
class SizeFactor(fish: Fish) : FishFactor(fish) {

    var min = FishSettingVariable("size.min", 0.0)
    var max = FishSettingVariable("size.max", 1.0)

    fun generateRandomSize():Pair<Double,SizeRank> {
        if(max.get() < min.get()) return Pair(-1.0,SizeRank.NORMAL)
        val min=min.get()
        val max=max.get()
        var sizeRank=SizeRank.NORMAL

        //1.97は97.5%の場所
        val gaussiann=java.util.Random().nextGaussian()

        when {
            (gaussiann<-1.645)->sizeRank=SizeRank.SMALL
            (gaussiann>1.645)->sizeRank=SizeRank.BIG
        }

        if(gaussiann>1.64)sizeRank=SizeRank.BIG
        var size=gaussiann*((max-min)/2/1.97)+(min+max)/2
        if(size<=0)size=min
        return Pair(round(size*10) / 10,sizeRank)
    }


}