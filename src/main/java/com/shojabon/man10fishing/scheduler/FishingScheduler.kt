package com.shojabon.man10fishing.scheduler

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.contest.AbstractFishContest
import com.shojabon.man10fishing.contest.FishContestPlayer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class FishingScheduler {

    val timings = ArrayList<Timing>()

    fun next(now: Calendar){
        timings.forEach {
            if (it.month != null && it.month != now.get(Calendar.MONTH)){
                return@forEach
            }
            if (it.week != null && it.week != now.get(Calendar.DAY_OF_WEEK_IN_MONTH)){
                return@forEach
            }
            if (it.day != null && it.day != now.get(Calendar.DAY_OF_MONTH)){
                return@forEach
            }
            if (it.hour != null && it.hour != now.get(Calendar.HOUR_OF_DAY)){
                return@forEach
            }
            if (it.minute != null && it.minute != now.get(Calendar.MINUTE)){
                return@forEach
            }
            if (it.dayOfTheWeek != null && it.dayOfTheWeek!!.int != now.get(Calendar.DAY_OF_WEEK)){
                return@forEach
            }

            it.action.invoke()
        }
    }

    class Timing {
        var dayOfTheWeek: DayOfTheWeek? = null
        var month: Int? = null
        var week: Int? = null
        var day: Int? = null
        var hour: Int? = null
        var minute: Int? = null
        var name: String? = null
        lateinit var action: Action

        fun setConfig(config: Map<*, *>): Timing {
            dayOfTheWeek = DayOfTheWeek.getDayOfTheWeek(config["dayOfTheWeek"] as? String)

            month = config["month"] as? Int

            week = config["week"] as? Int

            day = config["day"] as? Int

            val configTime = config["time"] as? String
            if (configTime != null){
                val split = configTime.split(":")
                hour = split.getOrNull(0)?.toIntOrNull()?:0
                minute = split.getOrNull(1)?.toIntOrNull()?:0
            }

            name = config["name"] as? String?:config["fileName"] as String

            action = Action(ActionEnum.getActionEnum(
                (config["action"] as Map<*, *>)["type"] as String)!!,
                (config["action"] as Map<*, *>)["value"] as String)

            return this
        }

        inner class Action(private val action: ActionEnum, private val actionValue: String){

            private val prefix = "§7[§cMFishScheduler§7]§r"

            private fun startContest(contestName: String){
                val contest = AbstractFishContest.newInstance(contestName)
                if (contest == null){
                    Bukkit.broadcast(Component.text("$prefix§c§lコンテストが存在しません §7$name"),Server.BROADCAST_CHANNEL_ADMINISTRATIVE)
                    return
                }

                if (Man10Fishing.nowContest != null){
                    Bukkit.broadcast(Component.text("$prefix§c§lコンテストが開始されています §7$name"),Server.BROADCAST_CHANNEL_ADMINISTRATIVE)
                    return
                }

                Bukkit.getOnlinePlayers().forEach {
                    contest.players[it.uniqueId] = FishContestPlayer(it.uniqueId, it.name)
                }

                contest.start()
            }

            fun invoke(){
                when(action){
                    ActionEnum.START_CONTEST->{
                        startContest(actionValue)
                    }
                    ActionEnum.RANDOM_START_CONTEST->{

                        val pick = actionValue.split(",").randomOrNull()

                        if (pick == null){
                            Bukkit.broadcast(Component.text("$prefix§c§lランダムなコンテストを取れませんでした §7$name"))
                            return
                        }

                        startContest(pick)
                    }
                    ActionEnum.MESSAGE->{
                        Bukkit.broadcast(Component.text(
                            actionValue
                                .replace("&","§")
                                .replace("<and>","&"))
                            , Server.BROADCAST_CHANNEL_USERS)
                    }
                }
            }
        }

        enum class ActionEnum{
            START_CONTEST,
            RANDOM_START_CONTEST,
            MESSAGE;

            companion object{
                fun getActionEnum(string: String?): ActionEnum? {
                    return try {
                        ActionEnum.valueOf((string?:return null).toUpperCase())
                    } catch (e: Exception){
                        null
                    }
                }
            }
        }

        enum class DayOfTheWeek(val int: Int){
            SUNDAY(1),
            MONDAY(2),
            TUESDAY(3),
            WEDNESDAY(4),
            THURSDAY(5),
            FRIDAY(6),
            SATURDAY(7);

            companion object{
                fun getDayOfTheWeek(string: String?): DayOfTheWeek? {
                    return try {
                        DayOfTheWeek.valueOf((string?:return null).toUpperCase())
                    } catch (e: Exception){
                        null
                    }
                }
            }
        }
    }

    companion object{
        fun newInstance(name: String): FishingScheduler? {
            val scheduler = FishingScheduler()
            return try {
                val config = YamlConfiguration.loadConfiguration(File("${Man10Fishing.instance.dataFolder.path}/schedulers/${name}.yml"))

                config.getMapList("timings").map { HashMap(it) }.forEach {
                    it["fileName"] = name
                    scheduler.timings.add(Timing().setConfig(it))
                }
                scheduler.timings.forEach {
                    if (it.month == null && it.day == null && it.dayOfTheWeek == null && it.hour == null && it.minute == null){
                        Man10Fishing.instance.logger.warning("Scheduler Error. 月、日、時間、または曜日を必ず指定してください")
                        return@forEach
                    }
                }
                scheduler
            } catch (e: Exception) {
                null
            }

        }
    }
}