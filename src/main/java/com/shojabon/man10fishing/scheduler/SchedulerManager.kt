package com.shojabon.man10fishing.scheduler

import java.util.Calendar

class SchedulerManager: Thread() {

    val schedulers = ArrayList<FishingScheduler>()

    override fun run() {
        while (true){
            val calendar = Calendar.getInstance()
            schedulers.forEach {
                it.next(calendar)
            }

            sleep(60000)
        }
    }
}