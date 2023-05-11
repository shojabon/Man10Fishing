package com.shojabon.man10fishing.scheduler

class SchedulerManager: Thread() {

    val schedulers = ArrayList<FishingScheduler>()

    override fun run() {
        while (true){
            schedulers.forEach {
                it.next()
            }

            sleep(60000)
        }
    }
}