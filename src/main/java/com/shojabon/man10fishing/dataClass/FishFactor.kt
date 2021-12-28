package com.shojabon.man10fishing.dataClass

import org.bukkit.entity.Player
import com.shojabon.man10fishing.dataClass.FishingRod
import com.shojabon.man10fishing.annotations.FishFactorDefinition

abstract class FishFactor(var fish: Fish) {
    fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod): Boolean {
        return true
    }

    fun rarityMultiplier(fish: Fish, currentMultiplier: Float, fisher: Player, rod: FishingRod): Float {
        return 1f
    }

    val definition: FishFactorDefinition?
        get() = if (!this.javaClass.isAnnotationPresent(FishFactorDefinition::class.java)) null else this.javaClass.getAnnotation(FishFactorDefinition::class.java)

    open fun onFish(fish: Fish, fisher: Player, rod: FishingRod) {}
}