package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
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

    fun generateRandomSize(): Double {
        if(max.get() < min.get()) return -1.0
        val min=min.get()
        val max=max.get()

        //1.97は97.5%の場所
        var size=java.util.Random().nextGaussian()*((max-min)/1.97)+(min+max)/2
        if(size<=0)size=min
        return round(size*10) / 10
    }


}