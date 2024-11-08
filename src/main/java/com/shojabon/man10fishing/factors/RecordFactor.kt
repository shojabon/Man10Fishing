package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10FishingAPI
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import org.bukkit.Material
import org.bukkit.entity.Player

@Author(author = "JToTl")
@FishFactorDefinition(name = "レコード",
        iconMaterial = Material.MUSIC_DISC_11,
        explanation = ["サーバー全体における魚のレコードに関するロジック"],
        settable = false)
class RecordFactor(fish : Fish) : FishFactor(fish) {

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        Man10FishingAPI.fishRecords[fish.name]?.let {
            if(it.size<parameter.size)Man10FishingAPI.fishRecords[fish.name]= FishRecordData(fisher.uniqueId,parameter.size)
        }?: kotlin.run { Man10FishingAPI.fishRecords[fish.name]= FishRecordData(fisher.uniqueId,parameter.size) }
    }
}