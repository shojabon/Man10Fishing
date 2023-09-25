package com.shojabon.man10fishing.dataClass

import org.bukkit.entity.Player
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import org.bukkit.Location

abstract class FishFactor(var fish: Fish) {
    open fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod, hookLocation:Location): Boolean {
        return true
    }

    open fun rarityMultiplier(fish: Fish, currentMultiplier: Float, fisher: Player, rod: FishingRod): Float {
        return currentMultiplier
    }

    open fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
    }

    val definition: FishFactorDefinition?
        get() = if (!this.javaClass.isAnnotationPresent(FishFactorDefinition::class.java)) null else this.javaClass.getAnnotation(FishFactorDefinition::class.java)

}