package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.Fish
import com.shojabon.man10fishing.dataClass.FishFactor
import com.shojabon.man10fishing.dataClass.FishSettingVariable
import com.shojabon.man10fishing.dataClass.FishingRod
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "コマンド",
        iconMaterial = Material.COMMAND_BLOCK,
        explanation = ["釣ったときに実行されるコマンド"],
        settable = true)
/*
    commands: コマンドその１,コマンドその２,...
 */
class CommandFactor(fish: Fish) : FishFactor(fish) {

    val commands=FishSettingVariable("commands", null as List<String>?)

    override fun onFish(fish: Fish, fisher: Player, rod: FishingRod) {
        for(command in commands.get()?:return){
            fisher.performCommand(command)
        }
    }


}