package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "コマンド",
        iconMaterial = Material.COMMAND_BLOCK,
        explanation = ["釣ったときに実行されるコマンド"],
        adminSetting = true,
        settable = true)
/*
    commands: コマンドその１,コマンドその２,...
    <player> で釣った人
    <fish> で釣った魚
    <rarity> で釣ったレアリティ
    <world> で釣ったワールド名
 */
class CommandFactor(fish: Fish) : FishFactor(fish) {

    val commands=FishSettingVariable("command", null as List<String>?)

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        for(command in commands.get()?:return){
            Bukkit.dispatchCommand(Bukkit.getServer().consoleSender,command
                    .replace("<player>",fisher.name)
                    .replace("<fish>",fish.name)
                    .replace("<rarity>",fish.rarity)
                    .replace("<world>",fisher.world.name))
        }
    }


}