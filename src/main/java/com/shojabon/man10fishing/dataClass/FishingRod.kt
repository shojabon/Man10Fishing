package com.shojabon.man10fishing.dataClass

import org.bukkit.inventory.ItemStack
import java.util.*

class FishingRod(var rod: ItemStack) {
    var remainingFoodCount = 0
    var currentFood = ArrayList(listOf(0.0, 0.0, 0.0, 0.0, 0.0))
}