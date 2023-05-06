package com.shojabon.man10fishing.dataClass

import org.bukkit.Material

class FishRarity(var name: String, var alias: String, var weight: Int, var material: Material,val namePrefix:String,val loreDisplayName:String){
    val fishInGroup = ArrayList<Fish>()
}