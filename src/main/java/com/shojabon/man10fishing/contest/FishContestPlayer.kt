package com.shojabon.man10fishing.contest

import com.shojabon.man10fishing.dataClass.FishParameter
import java.util.UUID

class FishContestPlayer {
    lateinit var uuid: UUID
    var name = ""
    val caughtFish = ArrayList<FishParameter>()
}