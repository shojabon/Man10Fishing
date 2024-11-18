package com.shojabon.man10fishing.dataClass

import org.bukkit.Material

class FishRarity(var name: String, var alias: String, var weight: Int,
                 var material: Material, val namePrefix: String, val loreDisplayName: String,
                 var enabledItemIndex: Boolean = true,
                 var minSellPrice: Double = 0.0, var priceMultiplier: Double = 0.0,val broadcast:Boolean,val firework:Boolean) {
    val fishInGroup = ArrayList<Fish>()
}