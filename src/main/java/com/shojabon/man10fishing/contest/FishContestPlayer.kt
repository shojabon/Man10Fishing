package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import java.util.UUID

class FishContestPlayer() {

    constructor(uuid: UUID, name: String) : this() {
        this.uuid = uuid
        this.name = name
    }

    lateinit var uuid: UUID
    var name = ""
    val caughtFish = ArrayList<FishParameter>()
    var allowedCaughtFish=ArrayList<FishParameter>()

    fun addAllowedCaughtFish(fish:FishParameter){
        allowedCaughtFish.add(fish)
    }
    fun getMaxSizeFish():FishParameter?{
        return caughtFish.maxByOrNull { it.size }
    }

    fun getMinSizeFish():FishParameter?{
        return caughtFish.minByOrNull { it.size }
    }

    fun getMaxSizeAllowedFish():FishParameter?{
        return allowedCaughtFish.maxByOrNull { it.size }
    }

    fun getMinSizeAllowedFish():FishParameter?{
        return allowedCaughtFish.minByOrNull { it.size }
    }
}