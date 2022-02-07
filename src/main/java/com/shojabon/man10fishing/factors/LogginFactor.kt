package com.shojabon.man10fishing.factors

import com.shojabon.man10fishing.Man10Fishing
import com.shojabon.man10fishing.annotations.Author
import com.shojabon.man10fishing.annotations.FishFactorDefinition
import com.shojabon.man10fishing.dataClass.*
import com.shojabon.man10fishing.itemindex.ItemIndex
import org.bukkit.Material
import org.bukkit.entity.Player
import java.text.SimpleDateFormat

@Author(author = "tororo_1066")
@FishFactorDefinition(name = "データベース",
    iconMaterial = Material.BOOK,
    explanation = ["データベースに保存するロジック"],
    settable = false)
class LogginFactor(fish : Fish) : FishFactor(fish) {

    val index = FishSettingVariable("index",-1)

    override fun onFish(fish: Fish, parameter: FishParameter, fisher: Player, rod: FishingRod) {
        if (index.get() == -1)return

        if (!Man10Fishing.mysql.execute("insert into fish_log (fish, rarity, date_time, name, uuid, weight, size) VALUES " +
                    "('${fish.name}', '${fish.rarity}', '${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parameter.dateTime)}', '${fisher.name}', '${fisher.uniqueId}', ${parameter.weight}, ${parameter.size})")){
            fisher.sendMessage(Man10Fishing.prefix + "§4データベースエラー。 運営に報告してください error code 1")
        }
    }
}