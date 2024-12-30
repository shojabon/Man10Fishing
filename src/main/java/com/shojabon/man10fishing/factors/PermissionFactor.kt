package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import com.shojabon.man10fishing.dataClass.FishingRod
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "権限",
        iconMaterial = Material.COMMAND_BLOCK,
        explanation = ["必要な権限"],
        adminSetting = false,
        settable = true)
class PermissionFactor(fish: Fish) : FishFactor(fish) {

    private val permission=FishSettingVariable("permission",null as String)

    override fun fishEnabled(fish: Fish, fisher: Player, rod: FishingRod, hookLocation: Location): Boolean {
        return permission.get()?.let { fisher.hasPermission(it) }?:false
    }

}