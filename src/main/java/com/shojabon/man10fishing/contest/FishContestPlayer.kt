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
}